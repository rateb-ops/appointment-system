package com.project.appointmentsystem.Dto;

import com.project.appointmentsystem.entity.Role;
import jakarta.validation.constraints.Email;

public class Register {

        private String name;
        @Email(message = "you must enter the vaild email")
        private String email;
        private String password;
        private Role role;
        public Register() {}

        public String getName() {
                return name;
        }
        public void setName(String name) {
                this.name = name;
        }
        public String getEmail() {
                return email;
        }
        public void setEmail(String email) {

                this.email = email;
        }
        public String getPassword() {

                return password;
        }
        public void setPassword(String password) {

                this.password = password;
        }

        public Role getRole() {
                return role;
        }

        public void setRole(Role role) {
                this.role = role;
        }
}
