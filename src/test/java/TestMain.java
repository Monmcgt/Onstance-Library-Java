import me.monmcgt.code.onstance.library.java.OnstanceConnector;

public class TestMain {
    public static void main(String[] args) {
        String uid = System.getProperty("onstance.uid");
        if (uid == null) {
            uid = "555555555555555555555555555555555555555555555555";
        }
        OnstanceConnector onstanceConnector = new OnstanceConnector(56790, uid, (response) -> {
            System.out.println(response.toString());
        });
        onstanceConnector.start();
    }
}
