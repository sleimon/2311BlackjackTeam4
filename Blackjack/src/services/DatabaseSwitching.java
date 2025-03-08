public class DatabaseSwitching {
    private static final boolean USE_SUPABASE =  Boolean.parseBoolean(System.getenv("USE_SUPABASE")); // Set to true for Supabase, false for StubDatabase

    public static DatabaseService getDatabaseService() {
        if (USE_SUPABASE) {
            return new UserService();  // This will use Supabase
        } else {
            return new StubDatabase();  // This will use the stub database
        }
    }

}
