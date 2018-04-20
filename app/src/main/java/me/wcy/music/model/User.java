package me.wcy.music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1;

    @Property(nameInDb = "birthday")
    private String birthday;
    @Property(nameInDb = "area")
    private String area;
    @Property(nameInDb = "password")
    private String password;
    @Property(nameInDb = "code")
    private int code;
    @Property(nameInDb = "isOnLine")
    private int isOnLine;
    @Property(nameInDb = "signature")
    private String signature;
    @Property(nameInDb = "sex")
    private String sex;
    @Property(nameInDb = "phoneNum")
    private String phoneNum;
    @Property(nameInDb = "userName")
    private String userName;
    @Id(autoincrement = true)
    @Property(nameInDb = "userID")
    private long userID;
    @Property(nameInDb = "age")
    private int age;

    public User() {

    }

    public User(String birthday, String area, String password, int code, int isOnLine, String signature, String sex, String phoneNum, String userName, int userID, int age) {
        this.birthday = birthday;
        this.area = area;
        this.password = password;
        this.code = code;
        this.isOnLine = isOnLine;
        this.signature = signature;
        this.sex = sex;
        this.phoneNum = phoneNum;
        this.userName = userName;
        this.userID = userID;
        this.age = age;
    }

    @Generated(hash = 1301299514)
    public User(String birthday, String area, String password, int code, int isOnLine, String signature, String sex, String phoneNum, String userName, long userID, int age) {
        this.birthday = birthday;
        this.area = area;
        this.password = password;
        this.code = code;
        this.isOnLine = isOnLine;
        this.signature = signature;
        this.sex = sex;
        this.phoneNum = phoneNum;
        this.userName = userName;
        this.userID = userID;
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getIsOnLine() {
        return isOnLine;
    }

    public void setIsOnLine(int isOnLine) {
        this.isOnLine = isOnLine;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "birthday='" + birthday + '\'' +
                ", area='" + area + '\'' +
                ", password='" + password + '\'' +
                ", code=" + code +
                ", isOnLine=" + isOnLine +
                ", signature='" + signature + '\'' +
                ", sex='" + sex + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", userName='" + userName + '\'' +
                ", userID=" + userID +
                ", age=" + age +
                '}';
    }
}
