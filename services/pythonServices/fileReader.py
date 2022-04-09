from gen_py.healthMessage.ttypes import Message, RamData, DiskData
from gen_py.healthMessage import HealthMessage

class FileReader:


    def __init__(self, file_paths:list):
        self.__current_index= 0
        self.__paths= file_paths
        self.__current_file= None
    

    def read_object(self):
        self.__check_file()
        string= self.__current_file.read(1).decode("utf-8")
        if not len(string):
            self.__current_file = None
            return self.read_object()
        counter= 1 # It starts with {

        while counter != 0:
            letter= self.__current_file.read(1).decode("utf-8")
            if letter == '{':
                    counter +=1
            elif letter == '}':
                    counter -=1
            string += letter
        return self.__get_object_from_string(string)

                
    

    def __check_file(self):
        if self.__current_file is None:
            if self.__current_index == len(self.__paths):
                print('ENDED')
                return None
            self.__current_file= open(self.__paths[self.__current_index],'rb')
            self.__current_index +=1
            print(f'Index becomes {self.__current_index}')


    def __get_object_from_string(self,string):
        data= string.split(":")
        service= data[1]
        service= service.split("'")[1]
        
        timeStamp= int(data[2].split(",")[0])
        cpu= float(data[3].split(",")[0])

        ram= float(data[5].split(",")[0])
        disk= float(data[8].split(",")[0])
        # print(disk)
        ramm= RamData()
        ramm.Total= ram
        disks= DiskData()
        disks.Total=disk
        message= Message(service,str(timeStamp),cpu,ramm,disks)
        
        return message
