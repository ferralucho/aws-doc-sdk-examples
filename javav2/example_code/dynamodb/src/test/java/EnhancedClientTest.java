import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnhancedClientTest {

    private static DynamoDbClient ddb;
    private static DynamoDbEnhancedClient enhancedClient;
    private static String enhancedTableName = "";
    private static String enhancedTableKey  = "";

    @BeforeAll
    public static void setUp() throws IOException {

        //Create a DynamoDbClient object
        Region region = Region.US_EAST_1;
        ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        // Create a DynamoDbEnhancedClient object
        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        try (InputStream input = EnhancedClientTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            enhancedTableName = prop.getProperty("enhancedTableName");
            enhancedTableKey = prop.getProperty("enhancedTableKey");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingEnhancedClient_thenNotNull() {
        assertNotNull(enhancedClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateTable() {

       System.out.println("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void PutItem() {

        try {
            //Lets wait 20 secs for table to complete
            TimeUnit.SECONDS.sleep(20);
            EnhancedPutItem.putRecord(enhancedClient);

        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void PutBatchItems() {

       EnhancedBatchWriteItems.putBatchRecords(enhancedClient);
       System.out.println("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetItem() {

      String result = EnhancedGetItem.getItem(enhancedClient);
      assertTrue(!result.isEmpty());
      System.out.println("\n Test 5 passed");
    }

    @Test
    @Order(6)
    public void QueryRecords() {

        String result = EnhancedQueryRecords.queryTable(enhancedClient);
        assertTrue(!result.isEmpty());
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void ScanRecords() {

       EnhancedScanRecords.scan(enhancedClient);
       System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteTable() {

       DeleteTable.deleteDynamoDBTable(ddb,enhancedTableName);
       System.out.println("\n Test 8 passed");
    }
}