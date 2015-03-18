package propagate.com.propagate_client.contact;

import java.io.Serializable;

/**
 * Created by kaustubh on 22/1/15.
 */
public class Contact implements Comparable<Contact>,Serializable {

  private long contact_id;
  private String name;
  private String phone_number;
  private String profile_pic;
  private boolean checked = false;

  public Contact(long contact_id, String name, String phone_number, String profile_pic , Boolean checked) {
    this.contact_id = contact_id;
    this.name = name;
    this.phone_number = phone_number;
    this.profile_pic = profile_pic;
    this.checked = checked;
  }

  public Contact(long contact_id,String name,String phone_number){
    this.contact_id = contact_id;
    this.name = name;
    this.phone_number = phone_number;
  }

  public long getContact_id() {
    return contact_id;
  }

  public void setContact_id(long contact_id) {
    this.contact_id = contact_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone_number() {
    return phone_number;
  }

  public void setPhone_number(String phone_number) {
    this.phone_number = phone_number;
  }

  public String getProfile_pic() {
    return profile_pic;
  }

  public void setProfile_pic(String profile_pic) {
    this.profile_pic = profile_pic;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public void toggleChecked() {
    checked = !checked ;
  }

  @Override
  public int compareTo(Contact contact) {
    return this.getName().compareTo(contact.getName());
  }

}
