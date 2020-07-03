package com.example.stud;

public class ClassListItems {



        public String img; //Image URL
        public String name; //Name

        public ClassListItems(String name, String img)
        {
            this.img = img;
            this.name = name;
        }

        public String getImg() {
            return img;
        }

        public String getName() {
            return name;
        }
    }

