import java.sql.Timestamp;

public class Customer {
    String uuid;
    String car;
    int parkingSpotId;
    Timestamp enterTimestamp;
    Timestamp leaveTimestamp;
    float price;
    long durationInMiliseconds;
}
