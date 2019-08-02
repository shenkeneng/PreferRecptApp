package com.frxs.wmsrecpt.model;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/07
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class UserInfo implements Serializable {

    /**
     * EmpID : 0
     * OpAreaID : 0
     * WID : 0
     * OpAreaName : string
     * WName : string
     * EmpName : string
     * UserAccount : string
     * UserMobile : string
     * Password : string
     * PasswordSalt : string
     * IsLocked : 0
     * IsDeleted : 0
     * UserType : 0
     * SubWarehouses : [{"SubWID":0,"SubWName":"string","WID":0,"OpAreaID":0,"IsDeleted":0,"ModifyTime":"2018-11-07T11:17:05.984Z","ModifyUserID":0,"ModifyUserName":"string","SubType":0}]
     */

    private int EmpID;
    private int OpAreaID;
    private int WID;
    private String OpAreaName;
    private String WName;
    private String EmpName;
    private String UserAccount;
    private String UserMobile;
    private String Password;
    private String PasswordSalt;
    private int IsLocked;
    private int IsDeleted;
    private int UserType;
    private List<SubWarehousesBean> SubWarehouses;
    private String Token;

    public int getEmpID() {
        return EmpID;
    }

    public void setEmpID(int EmpID) {
        this.EmpID = EmpID;
    }

    public int getOpAreaID() {
        return OpAreaID;
    }

    public void setOpAreaID(int OpAreaID) {
        this.OpAreaID = OpAreaID;
    }

    public int getWID() {
        return WID;
    }

    public void setWID(int WID) {
        this.WID = WID;
    }

    public String getOpAreaName() {
        return OpAreaName;
    }

    public void setOpAreaName(String OpAreaName) {
        this.OpAreaName = OpAreaName;
    }

    public String getWName() {
        return WName;
    }

    public void setWName(String WName) {
        this.WName = WName;
    }

    public String getEmpName() {
        return EmpName;
    }

    public void setEmpName(String EmpName) {
        this.EmpName = EmpName;
    }

    public String getUserAccount() {
        return UserAccount;
    }

    public void setUserAccount(String UserAccount) {
        this.UserAccount = UserAccount;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String UserMobile) {
        this.UserMobile = UserMobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPasswordSalt() {
        return PasswordSalt;
    }

    public void setPasswordSalt(String PasswordSalt) {
        this.PasswordSalt = PasswordSalt;
    }

    public int getIsLocked() {
        return IsLocked;
    }

    public void setIsLocked(int IsLocked) {
        this.IsLocked = IsLocked;
    }

    public int getIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(int IsDeleted) {
        this.IsDeleted = IsDeleted;
    }

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int UserType) {
        this.UserType = UserType;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public List<SubWarehousesBean> getSubWarehouses() {
        return SubWarehouses;
    }

    public void setSubWarehouses(List<SubWarehousesBean> SubWarehouses) {
        this.SubWarehouses = SubWarehouses;
    }

    public static class SubWarehousesBean implements Serializable{
        /**
         * SubWID : 0
         * SubWName : string
         * WID : 0
         * OpAreaID : 0
         * IsDeleted : 0
         * ModifyTime : 2018-11-07T11:17:05.984Z
         * ModifyUserID : 0
         * ModifyUserName : string
         * SubType : 0
         */

        private int SubWID;
        private String SubWName;
        private int WID;
        private int OpAreaID;
        private int IsDeleted;
        private String ModifyTime;
        private int ModifyUserID;
        private String ModifyUserName;
        private int SubType;

        public int getSubWID() {
            return SubWID;
        }

        public void setSubWID(int SubWID) {
            this.SubWID = SubWID;
        }

        public String getSubWName() {
            return SubWName;
        }

        public void setSubWName(String SubWName) {
            this.SubWName = SubWName;
        }

        public int getWID() {
            return WID;
        }

        public void setWID(int WID) {
            this.WID = WID;
        }

        public int getOpAreaID() {
            return OpAreaID;
        }

        public void setOpAreaID(int OpAreaID) {
            this.OpAreaID = OpAreaID;
        }

        public int getIsDeleted() {
            return IsDeleted;
        }

        public void setIsDeleted(int IsDeleted) {
            this.IsDeleted = IsDeleted;
        }

        public String getModifyTime() {
            return ModifyTime;
        }

        public void setModifyTime(String ModifyTime) {
            this.ModifyTime = ModifyTime;
        }

        public int getModifyUserID() {
            return ModifyUserID;
        }

        public void setModifyUserID(int ModifyUserID) {
            this.ModifyUserID = ModifyUserID;
        }

        public String getModifyUserName() {
            return ModifyUserName;
        }

        public void setModifyUserName(String ModifyUserName) {
            this.ModifyUserName = ModifyUserName;
        }

        public int getSubType() {
            return SubType;
        }

        public void setSubType(int SubType) {
            this.SubType = SubType;
        }
    }
}
