
package io.thunder.utils.vson.other;


public class TempVsonOptions {

    private IVsonProvider[] dsf;
    private boolean legacyRoot;

    public TempVsonOptions() {
        this.dsf = new IVsonProvider[0];
        this.legacyRoot = true;
    }

    public IVsonProvider[] getDsfProviders() {
        return dsf.clone();
    }

    public void setDsfProviders(IVsonProvider[] value) {
        this.dsf = value.clone();
    }

    public boolean getParseLegacyRoot() {
        return this.legacyRoot;
    }

    public void setParseLegacyRoot(boolean value) {
        this.legacyRoot = value;
    }

    @Deprecated
    public boolean getEmitRootBraces() {
        return true;
    }

    @Deprecated
    public void setEmitRootBraces(boolean value) {

    }

}
