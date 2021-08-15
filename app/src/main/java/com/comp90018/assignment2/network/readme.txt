All network request methods could be here i.e. okhttp's

we could use static method;

or singleton, e.g.
public class GoodInfoNetworkManager {
    private static GoodInfoNetworkManager instance;

    public static synchronized GoodInfoNetworkManager getInstance() {
        if(instance == null) {
            instance = new GoodInfoNetworkManager();
        }

        return instance;
    }

    ...
}
