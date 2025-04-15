package liquidbase.plugin.action.function;

public class FunRegisterSQL {
    int apiFun;
    String registerSQL;

    public FunRegisterSQL(int apiFun, String registerSQL) {
        this.apiFun = apiFun;
        this.registerSQL = registerSQL;
    }

    public int getApiFun() {
        return apiFun;
    }

    public String getRegisterSQL() {
        return registerSQL;
    }

    public void setApiFun(int apiFun) {
        this.apiFun = apiFun;
    }

    public void setRegisterSQL(String registerSQL) {
        this.registerSQL = registerSQL;
    }
}
