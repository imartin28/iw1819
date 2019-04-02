package es.ucm.fdi.iw.model;

public enum FileType {
	Image("image", new String[] {"jpeg", "png"}),
	Video("video", new String[] {"mp4, ogg"}),
	Audio("audio", new String[] {"mpeg", "mp3"});
	
    private final String keyName;
    private String[] types;
	
	FileType(String keyName, String[] types) {
        this.keyName = keyName;
        this.types = types;
    }

    public String getKeyName() {
        return keyName;
    }
    
    public String[] getTypes() {
    	return types;
    }
    
    public static String getKeyName(String mimeType) {
    	String res = null;
    	FileType enumValues[] = FileType.values();

        int i = 0;
        boolean found = false;
        while(i < enumValues.length && !found) {
            found = (enumValues[i].hasType(mimeType));
            i++;
        }

        if(found)
            res = enumValues[i-1].getKeyName();

        return res;
    }
    
    public boolean hasType(String type) {
        int i = 0;
        boolean found = false;
        while(i < types.length && !found) {
        	found = (types[i].equalsIgnoreCase(type));
            i++;
        }
        
        return found;
    }
	
}
