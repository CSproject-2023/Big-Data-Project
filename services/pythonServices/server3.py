from email import message
from random import Random
import sys
from time import time,sleep
from gen_py.healthMessage.ttypes import Message, RamData, DiskData
from gen_py.healthMessage import HealthMessage
from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


RAM_MAX_SIZE= 16
DISK_MAX_SIZE=2048
SERVICE_NAME= 'Server3'
def main():
    # Make socket
    health_ip= input('Please enter ip of health server:')

    while True:
        try :
            transport = TSocket.TSocket(health_ip, 3500)
            # Buffering is critical. Raw sockets are very slow
            transport = TTransport.TBufferedTransport(transport)
            # Wrap in a protocol
            protocol = TBinaryProtocol.TBinaryProtocol(transport)
            # Create a client to use the protocol encoder
            client = HealthMessage.Client(protocol)
            # Connect!
            transport.open()
        except :
            print('Connection error! Trying again...')
            sleep(3)
            continue
            

        rand= Random()
        cpu = rand.random()

        ram_used= rand.random() * RAM_MAX_SIZE
        ram= RamData(ram_used, RAM_MAX_SIZE- ram_used)

        disk_used= rand.random()* DISK_MAX_SIZE
        disk= DiskData(disk_used, DISK_MAX_SIZE- disk_used)

        message= Message(SERVICE_NAME, str(time()),cpu,ram,disk)
        client.sendHealthMessage(message)
        print('Sent Successfully!')
        transport.close()
        sleep(0.2)


if __name__ == '__main__':
    try:
        main()
    except Thrift.TException as tx:
        print('%s' % tx.message)
