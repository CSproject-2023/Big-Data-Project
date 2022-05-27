import glob
from random import randint, random
import sys
from time import sleep
from gen_py.healthMessage import HealthMessage
from gen_py.healthMessage.HealthMessage import Message
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer
import pandas as pd
from pyspark.sql import SparkSession
from pyspark.sql.functions import explode
from pyspark.sql.functions import split

spark = SparkSession.builder.appName("StructuredNetworkWordCount").getOrCreate()
lines = spark.readStream .format("csv").option("format", "append").option('path','data.csv').load()

COLUMNS= ['ServiceName','Day','Minutes','Count','CPU_Util','Disk_Util','Ram_Util']
# df= None
class MessageHandler:
    def __init__(self):
        self.message = None
        self.df= None
    def sendHealthMessage(self,message:Message):
        print('Message Was received!')
        print(message.serviceName)
        self.message= message
        self.appendToDataFrame()
    
    def appendToDataFrame(self):
        dic= {
            'Service Name':self.message.serviceName,
            'Day':randint(0,364),
            'Minutes':randint(0,1339),
            'Count':0,
            'CPU_Util':self.message.CPU,
            'Disk_Util':self.message.Disk.Total,
            'Ram_Util':self.message.RAM.Total
        }
        dataframe= pd.DataFrame(dic, index=[0])
        print(dataframe)
        if self.df is None:
            self.df= dataframe
        else:
            self.df= pd.concat([self.df,dataframe],axis=0)
        print(self.df)

handler = MessageHandler()
processor = HealthMessage.Processor(handler)
transport = TSocket.TServerSocket(host='h-primary', port=8650)
tfactory = TTransport.TBufferedTransportFactory()
pfactory = TBinaryProtocol.TBinaryProtocolFactory()

server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

def receiveNewData():
    print('Starting the server...')
    server.serve()


if __name__ == '__main__':
    receiveNewData()
    print('done.')