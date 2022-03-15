namespace java healthMessage
namespace py healthMessage


struct RamData{
    1: double Total,
    2: double Free,
}
struct DiskData{
    1: double Total,
    2: double Free,
}
struct Message{
    1: string serviceName,
    2: string Timestamp,
    3: double CPU,
    4: RamData RAM,
    5: DiskData Disk,
}

service HealthMessage{
    oneway void sendHealthMessage(1: Message message);
}