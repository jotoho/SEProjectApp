create table if not exists Student(
    Matrikelnummer int primary key,
    Vorname varchar(100) not null,
    Nachname varchar(100) not null,
    EMail varchar(100)
)
#
create table if not exists Unternehmen(
    U_ID int primary key,
    Name varchar(100) not null
)
#
create table if not exists Ansprechpartner(
    A_ID int primary key,
    Vorname varchar(100) not null,
    Nachname varchar(100) not null,
    EMail varchar(100),
    Arbeitgeber int not null,
    foreign key (Arbeitgeber) references Unternehmen(U_ID)
)
#
create table if not exists Projekt(
    P_ID int primary key,
    Student_1 int,
    Student_2 int,
    Student_3 int,
    Ansprechpartner int,
    foreign key (Student_1) references Student(Matrikelnummer),
    foreign key (Student_2) references Student(Matrikelnummer),
    foreign key (Student_3) references Student(Matrikelnummer),
    foreign key (Ansprechpartner) references Ansprechpartner(A_ID)
)