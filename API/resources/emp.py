from flask_restful import Resource,reqparse
from db import query
from werkzeug.security import safe_str_cmp
from flask_jwt_extended import create_access_token,jwt_required
from datetime import datetime

class Profile(Resource):
    @jwt_required
    def get(self):
        try:
            return query("""SELECT * FROM project.profile""")
        except:
            return {"message":"There was an error connecting to profile table."},500

    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('stuid',type=int,required=True,help="student id cannot be left blank!")
        parser.add_argument('name',type=str,required=True,help="name cannot be left blank!")
        parser.add_argument('branch',type=str,required=True,help="branch cannot be left blank!")
        parser.add_argument('year',type=int,required=True,help="year cannot be left blank!")
        parser.add_argument('grade',type=float,required=True,help="grade cannot be left blank!")
        parser.add_argument('cactivities',type=str,required=True,help="cactivities cannot be left blank!")
        parser.add_argument('hobbies',type=int,required=True,help="Hobbies cannot be left blank!")
        parser.add_argument('phoneno',type=int,required=True,help="phonenp cannot be left blank!")
        parser.add_argument('emailid',type=str,required=True,help="emailid cannot be left blank!")
        data=parser.parse_args()
        try:
            query(f"""INSERT INTO project.profile (stuid,name,branch,year,grade,cactivities,hobbies,phoenno,emailid)
                                                    VALUES({data['stuid']},
                                                        '{data['name']}',
                                                        '{data['branch']}',
                                                        {data['year']},
                                                        '{data['grade']}',
                                                        '{data['cactivities']}',
                                                        '{data['hobbies']}',
                                                        '{data['phoenno']}',
                                                        '{data['emailid']}')""")
        except:
            return {"message":"There was an error inserting into profile table."},500

        return {"message":"Successfully Inserted."},201

class User():
    def __init__(self,username,password):
        self.username=username
        self.password=password

    @classmethod
    def getUserByUsername(cls,username):
        result=query(f"""SELECT username,password FROM superadmin WHERE username='{username}'""",return_json=False)
        if len(result)>0: return User(result[0]['username'],result[0]['password'])
        return None


class aUser():
    def __init__(self,username,password,clubname):
        self.username=username
        self.password=password
        self.clubname=clubname

    @classmethod
    def getAdminbyUserid(cls,username,clubname):
        #print(clubname)
        result=query(f"""SELECT uid,password,clubname FROM admin WHERE uid='{username}' and clubname='{clubname}' """,return_json=False)
        #print(result)
        if len(result)>0: return aUser(result[0]['uid'],result[0]['password'],result[0]['clubname'])
        return None

class Login(Resource):

    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('username',type=str,required=True,help="username cannot be left blank!")
        parser.add_argument('password',type=str,required=True,help="password cannot be left blank!")
        data=parser.parse_args()
        user=User.getUserByUsername(data['username'])
        if user and safe_str_cmp(user.password,data['password']):
            access_token=create_access_token(identity=user.username,expires_delta=False)
            return {'access_token':access_token},200
        return {"message":"Invalid Credentials!"}, 401

class Adminlog(Resource):
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('userid',type=int,required=True,help="userid cannot be left blank!")
        parser.add_argument('password',type=str,required=True,help="password cannot be left blank!")
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()
        #print(data['clubname'])
        user=aUser.getAdminbyUserid(data['userid'],data['clubname'])
        if user and safe_str_cmp(user.password,data['password']):
            access_token=create_access_token(identity=user.username,expires_delta=False)
            return {'access_token':access_token},200
        return {"message":"Invalid Credentials!"}, 401

class Changepassword(Resource):
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('otp',type=str,required=True,help="otp cannot be left blank!")
        parser.add_argument('password',type=str,required=True,help="password cannot be left blank!")
        data=parser.parse_args()
        try:
            result=query(f"""SELECT * FROM project.email where pin='{data['otp']}' """,return_json=False)
            query(f"""update admin set password='{data['password']}' where uid='{result[0]['stuid']}' """)
            query(f""" delete from email where stuid='{result[0]['stuid']}'""")
            return {'message':"Updation of password is succesfully done!"}
        except:
            return {"message":"Updation of password is not successful"},500

class ForgetPassword(Resource):
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('userid',type=int,required=True,help="userid name cannot be left blank!")
        parser.add_argument('otp',type=str,required=True,help="OTP not assigned")
        data=parser.parse_args()
        adminid = data['userid']
        otp=data['otp']
        try:
            result=query(f"""select emailid from project.profile where stuid={adminid} """,return_json=False)
            query(f""" insert into email (stuid,emailid,pin) values ({adminid},'{result[0]['emailid']}','{otp}')""")
            return result
        except:
            return {"message":"No entry with the given studentid "},500

class Addclub(Resource):
    @jwt_required
    def get(self):
        try:
            return query("""SELECT * FROM project.admin""")
        except:
            return {"message":"There was an error connecting to admin table."},500

    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('uid',type=int,required=True,help="id cannot be left blank!")
        parser.add_argument('username',type=str,required=True,help="username cannot be left blank!")
        parser.add_argument('password',type=str,required=True,help="password cannot be left blank!")
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()

        try:

            query(f"""INSERT INTO admin (uid,username,password,clubname)
                                                    VALUES('{data['uid']}','{data['username']}',
                                                        '{data['password']}','{data['clubname']}')""")


        except:

            return {"message":"There was an error inserting into admin table,bcoz the user has not registered in superadmin"},500

        try:
            query(f"""INSERT INTO project.student (cid,stuid,clubname,crole,acceptstatus)
                                                    VALUES(default,'{data['uid']}',
                                                        '{data['clubname']}','Admin',1)""")

        except:
            return {"message":"There was an error inserting admin into student table"},500

        try:

            query(f"""create table {data['clubname']} (clubid int primary key auto_increment,stuid int,eventname varchar(40),eventdate date)""")

        except:

            return {"message":"There was an error in creating the club"},500




class Allclubdetails(Resource):
    def get(self):
        try:
            query("""select * from student""")
        except:
            return {"message":"Unable to fetch all club details"}
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('cid',type=int,required=False,help="id can be left blank!")
        parser.add_argument('stuid',type=int,required=True,help="student id cannot be left blank!")
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        parser.add_argument('clubrole',type=str,required=True,help="club role cannot be left blank!")
        data=parser.parse_args()
        try:
            query(f"""insert into student (cid,stuid,clubname,clubrole) values('{data['cid']}','{data['stuid']}','{data['clubname']}','{data['clubrole']}')""")
        except:
            return {"Unable to insert in to student club table"}
        return {"Succefully inserted into student table"}


class Adminlogin(Resource):
    @jwt_required
    def get(self):
        try:
            return query("""SELECT * FROM project.admin""")
        except:
            return {"message":"There was an error connecting to admin table."},500

    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('uid',type=int,required=True,help="id cannot be left blank!")
        parser.add_argument('username',type=str,required=True,help="username cannot be left blank!")
        parser.add_argument('password',type=str,required=True,help="password cannot be left blank!")
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()

        try:
            query(f"""INSERT INTO project.admin (uid,username,password,clubname)
                                                    VALUES('{data['uid']}','{data['username']}',
                                                        '{data['password']}','{data['clubname']}')""")
        except:
            return {"message":"There was an error inserting into admin table,bcoz the user has not registered in superadmin"},500


        try:
            query(f"""create table {data['clubname']} (clubid int primary key auto_increment,stuid int,eventname varchar(40),eventdate date)""")
        except:
            return {"message":"There was an error in creating the club"},500
        return {"message":"Successfully Inserted and created."},201

class Clubdelete(Resource):
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('username',type=str,required=True,help="username cannot be left blank!")
        data=parser.parse_args()

        try:

            clubname=query(f"""select clubname from admin where username='{data['username']}'""",return_json=False)
            cname=clubname[0]['clubname']

            query(f"""delete from admin where username='{data['username']}'""")

            query("drop table if exists {}".format(cname))

            return {"message":"deleted"}
        except:
            return {"message":"tables are not deleted"},500
class Clubnames(Resource):
    #@jwt_required
    def get(self):
        try:
            return query(f"""select clubname from admin""")
        except:
            return {"message":"Unable to fetch club names"}
class Clubmembers(Resource):
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()
        clubname = data['clubname']
        try:

            return query(f"""select p.stuid,p.name,p.branch,s.crole,p.year from profile p,student s
             where s.clubname='{clubname}' and p.stuid=s.stuid and s.acceptstatus=1""")

        except:
            return {"message":"club members not returned"},500



class superadmin(Resource):
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('username',type=str,required=True,help="username cannot be left blank!")
        parser.add_argument('password',type=str,required=True,help="password cannot be left blank!")
        data=parser.parse_args()

        try:
            query(f"""insert into superadmin values('{data['username']}','{data['password']}')""")
        except:
            return {"Insertion into super admin table has falied"}
        return {"message":"Insertion is succesful in to super admin table"}

class Addclubmembers(Resource):
    #@jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('clubid',type=int,required=False,help="id can be left blank!")
        parser.add_argument('stuid',type=int,required=True,help="student id cannot be left blank!")
        parser.add_argument('eventname',type=str,required=True,help="eventname cannot be left blank!")
        parser.add_argument('eventdate',type=str,required=True,help="eventdate cannot be left blank!")
        data=parser.parse_args()
        try:
            query(f"""insert into cosc (clubid,stuid,eventname,eventdate) values('{data['clubid']}','{data['stuid']}','{data['eventname']}','{data['eventdate']}')""")
        except:
            return "error inserting into the respective club"
        return {"inserted data in to club succesfully"}

class Displaypostevents(Resource):
    def get(self):
        parser=reqparse.RequestParser()
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()
        #print(data['clubname'])
        try:

            return query(f"""select eventname,date_format(eventdate,"%Y-%m-%d"),description,venue,time_format(start,"%T"),time_format(end,"%T"),coordinator,contact from event where clubname='{data['clubname']}' """)
        except:

            return {"message":"Error in connecting to table"}
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        parser.add_argument('eventname',type=str,required=True,help="eventname cannot be left blank!")
        parser.add_argument('eventdate',type=str,required=True,help="eventdate cannot be left blank!")
        parser.add_argument('description',type=str,required=True,help="eventdate cannot be left blank!")
        parser.add_argument('venue',type=str,required=True,help="venue cannot be left blank!")
        parser.add_argument('start',type=str,required=True,help="start cannot be left blank!")
        parser.add_argument('end',type=str,required=True,help="end cannot be left blank!")
        parser.add_argument('coordinator',type=str,required=True,help="coordinator cannot be left blank!")
        parser.add_argument('contact',type=str,required=True,help="contact cannot be left blank!")
        data=parser.parse_args()
        date = data['eventdate']
        start = data['start']
        end = data['end']
        date = datetime.strptime(date,'%Y-%m-%d').date()
        start = start[0:5]
        end = end [0:5]
        start = datetime.strptime(start,'%H:%M')
        end = datetime.strptime(end,'%H:%M')
        try:
            query(f"""insert into event values('{data['clubname']}','{data['eventname']}','{date}','{data['description']}','{data['venue']}'
            ,'{start}','{end}','{data['coordinator']}','{data['contact']}')""")
        except:
            return {"message":"Error in inserting into event table"}


class Requesttoclub(Resource):
    def get(self):
        parser=reqparse.RequestParser()
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()
        #print(data)
        clubname = data['clubname']
        try:
            return query(f"""select p.stuid,p.name,p.branch,p.year from profile p,student s
             where s.clubname='{clubname}' and p.stuid=s.stuid and s.acceptstatus=-1 """)
        except:
            return {'message':"error in fetching details of student"}
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('cid',type=int,required=False,help="id can be left blank!")
        parser.add_argument('stuid',type=int,required=True,help="student id cannot be left blank!")
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        parser.add_argument('crole',type=str,required=True,help="club role cannot be left blank!")
        parser.add_argument('acceptstatus',type=int,required=True,help="acceptstatus cannot be left blank!")
        data=parser.parse_args()
        if(data['acceptstatus'] != -1):

            try:
                query(f"""update student set acceptstatus='{data['acceptstatus']}' where stuid='{data['stuid']}' and clubname='{data['clubname']}' """)
            except:
                return {'message':"Accept status are not changed"}
        else:
            try:
                query(f"""insert into student values(default,'{data['stuid']}','{data['clubname']}','{data['crole']},-1)' """)
            except:
                return {'message':"Not able to insert into student table"}

class Deletemembers(Resource):
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('stuid',type=int,required=True,help="student id cannot be left blank!")
        parser.add_argument('clubname',type=str,required=True,help="clubname cannot be left blank!")
        data=parser.parse_args()
        try:
            query(f"""delete from student where stuid= '{data['stuid']}' and clubname='{data['clubname']}' """)

        except:
            return {"message":"failed to delete"}

class EditAdmin(Resource):
    @jwt_required
    def post(self):
        parser=reqparse.RequestParser()
        parser.add_argument('newadminid',type=int,required=True,help="newadminid cannot be left blank!")
        parser.add_argument('admin_name',type=str,required=True,help="admin_name cannot be left blank!")
        data=parser.parse_args()
        newadminid = data['newadminid']
        admin_name=data['admin_name']
        try:
            result=query(f"""select uid from project.admin where username='{admin_name}' """,return_json=False)
            query(f""" update project.student set crole='Member' where stuid={result[0]['uid']}""")
            query(f""" update project.student set crole='Admin' where stuid={newadminid}""")
        except:
            return {"message":"No entry with the given studentid "},500
        try:
            name=query(f"""select name from project.profile where stuid={newadminid} """,return_json=False)
            #print(name[0]['name'])
            query(f""" update admin set uid={newadminid},username='{name[0]['name']}' where username='{admin_name}'""")

            return {"message":"Successfully Updated"}
        except:
            return {"message":"Unable to Update "},500

class Mail(Resource):
    @jwt_required
    def get(self):
        parser=reqparse.RequestParser()
        parser.add_argument('userid',type=int,required=False,help="userid name cannot be left blank!")
        parser.add_argument('username',type=str,required=False,help="username name cannot be left blank!")
        data=parser.parse_args()
        if data['userid'] is not None:
            adminid = data['userid']
            try:
                return query(f"""select emailid from project.profile where stuid={adminid} """,return_json=False)
            except:
                return {"message":"No entry with the given studentid "},500
        else:
            username = data['username']
            try:
                return query(f"""select emailid from project.profile  where name='{username}' """,return_json=False)
            except:
                return {"message":"No entry with the given studentid "},500
class Eventmembers(Resource):
    def get(self):
        parser=reqparse.RequestParser()
        parser.add_argument('clubname',type=str,required=False,help="club name cannot be left blank!")
        parser.add_argument('eventname',type=str,required=False,help="event name cannot be left blank!")
        parser.add_argument('eventdate',type=str,required=False,help="event date cannot be left blank!")
        data = parser.parse_args()
        date=data["eventdate"]
        date = datetime.strptime(date,'%Y-%m-%d').date()
        try:
            return query(f"""select p.stuid,p.name,p.branch,p.year from profile p,{data['clubname']} c
                where c.eventname='{data['eventname']}' and p.stuid=c.stuid and date_format(c.eventdate,"%Y-%m-%d") ='{date}' """)
        except:
            return {"message":"unable to fetch"},500
