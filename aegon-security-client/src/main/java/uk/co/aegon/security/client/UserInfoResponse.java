package uk.co.aegon.security.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/*
* Response Object
*/
@Data
public class UserInfoResponse implements Serializable{
    
	private static final long serialVersionUID = -1931051486791489718L;
	private List<UserInfoDTO> users;
    private String error;
    private Boolean displayLdapWarningFlag = Boolean.FALSE;

    public List<UserInfoDTO> getUsers() {
        return users;
    }

    public void addUser(UserInfoDTO user) {
        if ( this.users == null ) {
            this.users = new ArrayList<>();
        }
        this.users.add(user);
    }


}