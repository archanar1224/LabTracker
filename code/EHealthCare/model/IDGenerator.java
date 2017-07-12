package org.nimhans.EHealthCare.model;

public interface IDGenerator {
	public AssetId generateAssetID();
    public String generateNextRoot();
    public Boolean validateRoot(Request request);
    public boolean saveRoot(Request request);


}
