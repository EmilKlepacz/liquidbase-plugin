package liquidbase.plugin.action.module;

public class Module {
    int apimod;
    int apimodParent;
    String apimodPath;

    public void setApimod(int apimod) {
        this.apimod = apimod;
    }

    public void setApimodParent(int apimodParent) {
        this.apimodParent = apimodParent;
    }

    public int getApimod() {
        return apimod;
    }

    public int getApimodParent() {
        return apimodParent;
    }

    public void setApimodPath(String apimodPath) {
        this.apimodPath = apimodPath;
    }

    public String getApimodPath() {
        return apimodPath;
    }

    public Module(int apimod, int apimodParent, String apimodPath) {
        this.apimod = apimod;
        this.apimodParent = apimodParent;
        this.apimodPath = apimodPath;
    }
}
