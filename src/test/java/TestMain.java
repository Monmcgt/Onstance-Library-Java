import me.monmcgt.code.onstance.library.java.OnstanceConnector;

public class TestMain {
    public static void main(String[] args) {
        OnstanceConnector onstanceConnector = new OnstanceConnector("456544358544108", (response) -> {
            System.out.println(response.toString());
        });
        onstanceConnector.start();
    }
}
