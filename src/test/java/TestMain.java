import me.monmcgt.code.onstance.library.java.OnstanceConnector;

public class TestMain {
    public static void main(String[] args) {
        String uid = System.getProperty("onstance.uid");
        String port = System.getProperty("onstance.port");
        if (uid == null) {
            uid = "555555555555555555555555555555555555555555555555";
        }
        OnstanceConnector onstanceConnector = new OnstanceConnector(port != null ? Integer.parseInt(port) : 56780, uid, (response) -> {
            System.out.println(response.toString());
        });
        onstanceConnector.start();
    }
}
