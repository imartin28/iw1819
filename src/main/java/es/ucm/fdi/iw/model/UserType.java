package es.ucm.fdi.iw.model;

public enum UserType {

	Administrator("admin"),
	User("user");

    private final String keyName;

    UserType(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
    
    public static UserType getUserType(String keyName) {
    	UserType res = null;
    	UserType enumValues[] = UserType.values();

        int i = 0;
        boolean found = false;
        while(i < enumValues.length && !found) {
            found = (enumValues[i].getKeyName().equalsIgnoreCase(keyName));
            i++;
        }

        if(found)
            res = enumValues[i-1];

        return res;
    }
    
}
