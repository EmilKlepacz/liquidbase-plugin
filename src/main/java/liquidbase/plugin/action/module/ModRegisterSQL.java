package liquidbase.plugin.action.module;

public class ModRegisterSQL {
    int apiMod;
    String registerSQL;

    public ModRegisterSQL(int apiMod, String registerSQL) {
        this.apiMod = apiMod;
        this.registerSQL = registerSQL;
    }

    public int getApiMod() {
        return apiMod;
    }

    public void setApiMod(int apiMod) {
        this.apiMod = apiMod;
    }

    public String getRegisterSQL() {
        return registerSQL;
    }

    public void setRegisterSQL(String registerSQL) {
        this.registerSQL = registerSQL;
    }
}
