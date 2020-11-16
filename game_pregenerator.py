import catan_messaging_from_jsettlers_py_file_v3 as dqn
import pandas as pd
import random
import socket


class GamePregenerator():
    def __init__(self, host, port, output_file, save_interval = 100, timeout=None):
        self.host = host
        self.port = port
        self.output_file = output_file
        self.save_interval = save_interval
        self.timeout = timeout

        self.incomplete_games = 0
        self.unsaved_games = pd.DataFrame(columns=["firstPlacementMessage",
                                                    "firstPlacementChoice",
                                                    "secondPlacementMessage",
                                                    "secondPlacementChoice",
                                                    "endMessage"],
                                          dtype=str)

        with open(self.output_file, 'a') as fp:
            pass

    def write_to_file(self):
        self.unsaved_games.to_csv(self.output_file, mode='a', header=False, index=False)
        self.unsaved_games = self.unsaved_games[0:0]

    def save_msg(self, msg):
        if len(msg) == 5:
            new_row = pd.Series(msg[1:] + [msg[0]], index=self.unsaved_games.columns)
            self.unsaved_games = self.unsaved_games.append(new_row, ignore_index=True)
            if self.unsaved_games.shape[0] >= self.save_interval:
                self.write_to_file()
        else:
            print("Incomplete message, not saving...")

    def get_action(self, node_validity):
        available_filter = dqn.availability_filter(node_validity)
        possible_actions = []
        for index, validity in enumerate(available_filter):
            if validity == 1:
                possible_actions.append(index)
        action = random.choice(possible_actions)

        java_action = dqn.action_to_255(action)
        return java_action

    def handle_msg(self, msg):
        msg_history = []
        if not "end|" in msg:
            msg_args = msg.split("|")
        else:
            msg_history = msg.split("||")
            msg_args = msg_history[0].split("|")

        if msg_args[0] == "init_settlement":
            _, _, _, _, node_validity, _ = dqn.interpret_settlement_init_message(msg_args)
            action = self.get_action(node_validity)
            return action

        elif msg_args[0] == "end":
            if msg_args[1] == "true":
                self.save_msg(msg_history)
            else:
                self.incomplete_games += 1
                print("Incomplete game (total: {}), ignoring...".format(self.incomplete_games))

        else:
            print("Unexpected message received, ignoring...")

        return None

    def run(self):
        soc = socket.socket()  # Create a socket object
        if self.timeout:
            soc.settimeout(self.timeout)
        try:
            soc.bind((self.host, self.port))

        except socket.error as err:
            print('Bind failed. Error Code : '.format(err))

        soc.listen(10)
        print("Socket Listening ... ")
        while True:
            try:
                conn, addr = soc.accept()  # Establish connection with client.
                length_of_message = int.from_bytes(conn.recv(2), byteorder='big')
                msg = conn.recv(length_of_message).decode("UTF-8")
                print("Message Received ... ")
                action = self.handle_msg(msg)
                if not "end|" in msg:
                    conn.send((str(action) + '\n').encode(encoding='UTF-8'))
            except socket.timeout:
                print("Timeout or error occured. Exiting ... ")
                self.write_to_file()
                break


if __name__ == "__main__":
    server = GamePregenerator("localhost", 2004, "game_history.csv", save_interval=100, timeout=60)
    server.run()