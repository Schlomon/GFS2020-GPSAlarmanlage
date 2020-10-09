import socket
import sqlite3
import time
import json

pathToDB = r"/home/pi/sqlite3/geoData.db"

def create_connection(db_file):
    """ create a database connection to the SQLite database
        specified by db_file
    :param db_file: database file
    :return: Connection object or None
    """
    conn = None
    try:
        conn = sqlite3.connect(db_file)
    except sqlite3.Error as e:
        print(e)

    return conn

def insert_coord(conn, db_data):
    """
    Insert a new coordination into the coordinations table
    :param conn: connection
    :param db_data: phone number, coord, time, temperature
    """
    lat, lng = db_data[1].replace(" ", "").split(",")
    c_data = [db_data[0], lat, lng, db_data[2], db_data[3]]
    sql_command = '''INSERT INTO coordinates(tel_number, lat, long, date, temperatur) VALUES(?,?,?,?,?)'''

    cur = conn.cursor()
    cur.execute(sql_command, c_data)


# Server
serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.bind(('', 8855))
serverSocket.listen(5)
while True:
    (clientSocket, address) = serverSocket.accept()
    recvString = ""

    print("connection from " + address[0])

    while True:
        try:
            data = clientSocket.recv(4096).decode('utf-8')
            if not data:
                break
            recvString = recvString + data
        except:
            print('Non utf-8 data received')
            recvString = ""
    print(recvString)
    if recvString != "":
        try:
            jsonObject = json.loads(recvString)

            db_connection = create_connection(pathToDB)

            if 'command' in jsonObject:
                # TODO implement web server to python
                with db_connection:
                    print("received {} from web server".format(jsonObject['command']))
            else:
                try:
                    with db_connection:
                        sender = jsonObject['sender']
                        coord = jsonObject['coordinate']
                        ti = jsonObject['time']
                        temp = jsonObject['temperature']
                        insert_coord(db_connection, [sender, coord, ti, temp])
                except KeyError as e:
                    print("No key found: " + e.args)

                print("added new coordination's and temperature")

            db_connection.close()

        except ValueError as e:
            print(e)
            print("not valid JSON received: " + recvString)
        except KeyError as e:
            print(e)
            print("One ore multiple keys couldn't be found. Please provide all of them")

    clientSocket.close()
    print("connection closed")
    time.sleep(5)

