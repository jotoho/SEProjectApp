create table if not exists Student(
    Matrikelnummer int primary key,
    Vorname varchar(100) not null,
    Nachname varchar(100) not null,
    EMail varchar(100)
)
#
create table if not exists Unternehmen(
    U_ID int primary key,
    Name varchar(100) not null,
    Adresse varchar(100) not null
)
#
create table if not exists Ansprechpartner(
    A_ID int primary key,
    Vorname varchar(100) not null,
    Nachname varchar(100) not null,
    Arbeitgeber int not null,
    foreign key (Arbeitgeber) references Unternehmen(U_ID)
)
#
create table if not exists Projekt(
    P_ID int primary key,
    Title varchar(100) not null,
    Beschreibung text not null,
    Kontext text not null,
    Outline text not null,
    Status varchar(100) not null,
    Feedback text not null,
    Student_1 int,
    Student_2 int,
    Student_3 int,
    Ansprechpartner int,
    foreign key (Student_1) references Student(Matrikelnummer),
    foreign key (Student_2) references Student(Matrikelnummer),
    foreign key (Student_3) references Student(Matrikelnummer),
    foreign key (Ansprechpartner) references Ansprechpartner(A_ID)
)
#
create table if not exists U_ID(
    ID integer primary key autoincrement
)
#
create table if not exists P_ID(
    ID integer primary key autoincrement
)
#
create table if not exists A_ID(
    ID integer primary key autoincrement
)
#
create trigger
    if not exists uid_insert
    after insert
    on Unternehmen
begin
    delete from U_ID where ID = NEW.U_ID;
end
#
create trigger
    if not exists uid_delete
    after delete
    on Unternehmen
begin
    insert into U_ID values (OLD.U_ID);
end
#
create trigger
    if not exists uid_refill
    after insert
    on Unternehmen
    when (select count(*) from U_ID) < 100
begin
    insert into U_ID(ID) values(null);
end
#
create trigger
    if not exists pid_insert
    after insert
    on Projekt
begin
    delete from P_ID where ID = NEW.P_ID;
end
#
create trigger
    if not exists pid_delete
    after delete
    on Projekt
begin
    insert into P_ID values (OLD.P_ID);
end
#
create trigger
    if not exists pid_refill
    after insert
    on Projekt
    when (select count(*) from P_ID) < 100
begin
    insert into P_ID(ID) values(null);
end
#
create trigger
    if not exists aid_insert
    after insert
    on Ansprechpartner
begin
    delete from A_ID where ID = NEW.A_ID;
end
#
create trigger
    if not exists aid_delete
    after delete
    on Ansprechpartner
begin
    insert into A_ID values (OLD.A_ID);
end
#
create trigger
    if not exists aid_refill
    after insert
    on Ansprechpartner
    when (select count(*) from A_ID) < 100
begin
    insert into A_ID(ID) values(null);
end
