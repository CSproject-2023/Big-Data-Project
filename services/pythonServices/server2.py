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

from fileReader import FileReader


RAM_MAX_SIZE= 6
DISK_MAX_SIZE=800
SERVICE_NAME= 'Server2'
def main():
    from_file= int(input('From number: '))
    to_file= int(input('To number: '))
    
    files= [f'/usr/local/labData/BigData/health_{i}.json' for i in range (from_file,to_file)]
    fileReader= FileReader(files)
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
            sleep(5)
            continue
            

        

        message=fileReader.read_object()
        if message is None:
            transport.close()
            return
        client.sendHealthMessage(message)
        # print('Sent Successfully!')
        transport.close()

if __name__ == '__main__':
    try:
        main()
    except Thrift.TException as tx:
        print('%s' % tx.message)
