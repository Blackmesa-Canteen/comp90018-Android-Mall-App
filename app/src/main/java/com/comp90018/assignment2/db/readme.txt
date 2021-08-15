For maintainability, we need to put all database access behaviors here.

we could use static method;

or singleton, e.g.
public class GoodInfoRepository {
    private static GoodInfoRepository instance;

    public static synchronized GoodInfoRepository getInstance() {
        if(instance == null) {
            instance = new GoodInfoRepository();
        }

        return instance;
    }

    ...
}
