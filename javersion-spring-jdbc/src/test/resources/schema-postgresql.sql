create table VERSION_TYPE (
  NAME varchar(8),
  primary key (NAME)
);
insert into VERSION_TYPE values ('NORMAL');
insert into VERSION_TYPE values ('RESET');

create table REPOSITORY (
  ID varchar(32) not null,
  ORDINAL bigint
);
insert into REPOSITORY (ID) values ('repository');

create table VERSION (
  DOC_ID varchar(255) not null,
  REVISION varchar(32) not null,
  ORDINAL bigint not null,
  TX varchar(32),

  BRANCH varchar(128) not null,
  TYPE varchar(8) not null,

  primary key (REVISION),

  constraint VERSION_TYPE_FK foreign key (TYPE) references VERSION_TYPE (name)
);

create sequence VERSION_ORDINAL_SEQ start with 1 increment by 1 no cycle;
create index VERSION_ORDINAL_IDX on VERSION (ORDINAL);
create index VERSION_TX_ORDINAL_IDX on VERSION (TX, ORDINAL) where TX is not null;
create index VERSION_DOC_ID_IDX on VERSION (DOC_ID) where TX is null;

create table VERSION_PARENT (
  REVISION varchar(32) not null,
  PARENT_REVISION varchar(32) not null,

  primary key (REVISION, PARENT_REVISION),

  constraint VERSION_PARENT_REVISION_FK
    foreign key (REVISION)
    references VERSION (REVISION),

  constraint VERSION_PARENT_PARENT_REVISION_FK
    foreign key (PARENT_REVISION)
    references VERSION (REVISION)
);

create table VERSION_PROPERTY (
  DOC_ID varchar(255) not null,
  REVISION varchar(32) not null,

  PATH varchar(512) not null,
  -- n=null, O=object, A=array, s=string,
  -- b=boolean, l=long, d=double, D=bigdecimal
  TYPE char(1),
  STR varchar(1024),
  NBR bigint,

  primary key (REVISION, PATH),

  constraint VERSION_PROPERTY_REVISION_FK
    foreign key (REVISION) references VERSION (REVISION)
);

create index VERSION_PROPERTY_DOC_ID_IDX on VERSION_PROPERTY (DOC_ID);
