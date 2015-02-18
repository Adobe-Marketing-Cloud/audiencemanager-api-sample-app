# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table audience_manager_authentication (
  email                     varchar(255) not null,
  access_token              varchar(255),
  refresh_token             varchar(255),
  constraint pk_audience_manager_authenticati primary key (email))
;

create table blog_post (
  id                        bigint not null,
  published                 timestamp,
  title                     varchar(255),
  content                   varchar(2048),
  author_email              varchar(255),
  constraint pk_blog_post primary key (id))
;

create table comment (
  id                        bigint not null,
  published                 timestamp,
  title                     varchar(255),
  content                   varchar(255),
  blog_post_id              bigint,
  author_email              varchar(255),
  constraint pk_comment primary key (id))
;

create table company_size (
  id                        bigint not null,
  label                     varchar(255),
  constraint pk_company_size primary key (id))
;

create table industry (
  id                        bigint not null,
  label                     varchar(255),
  constraint pk_industry primary key (id))
;

create table tag (
  label                     varchar(255) not null,
  creator_email             varchar(255),
  constraint pk_tag primary key (label))
;

create table user (
  email                     varchar(255) not null,
  name                      varchar(255),
  industry_id               bigint,
  company_size_id           bigint,
  password                  varchar(255),
  constraint pk_user primary key (email))
;


create table blog_post_tag (
  blog_post_id                   bigint not null,
  tag_label                      varchar(255) not null,
  constraint pk_blog_post_tag primary key (blog_post_id, tag_label))
;
create sequence audience_manager_authentication_seq;

create sequence blog_post_seq;

create sequence comment_seq;

create sequence company_size_seq;

create sequence industry_seq;

create sequence tag_seq;

create sequence user_seq;

alter table blog_post add constraint fk_blog_post_author_1 foreign key (author_email) references user (email) on delete restrict on update restrict;
create index ix_blog_post_author_1 on blog_post (author_email);
alter table comment add constraint fk_comment_blogPost_2 foreign key (blog_post_id) references blog_post (id) on delete restrict on update restrict;
create index ix_comment_blogPost_2 on comment (blog_post_id);
alter table comment add constraint fk_comment_author_3 foreign key (author_email) references user (email) on delete restrict on update restrict;
create index ix_comment_author_3 on comment (author_email);
alter table tag add constraint fk_tag_creator_4 foreign key (creator_email) references user (email) on delete restrict on update restrict;
create index ix_tag_creator_4 on tag (creator_email);
alter table user add constraint fk_user_industry_5 foreign key (industry_id) references industry (id) on delete restrict on update restrict;
create index ix_user_industry_5 on user (industry_id);
alter table user add constraint fk_user_companySize_6 foreign key (company_size_id) references company_size (id) on delete restrict on update restrict;
create index ix_user_companySize_6 on user (company_size_id);



alter table blog_post_tag add constraint fk_blog_post_tag_blog_post_01 foreign key (blog_post_id) references blog_post (id) on delete restrict on update restrict;

alter table blog_post_tag add constraint fk_blog_post_tag_tag_02 foreign key (tag_label) references tag (label) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists audience_manager_authentication;

drop table if exists blog_post;

drop table if exists blog_post_tag;

drop table if exists comment;

drop table if exists company_size;

drop table if exists industry;

drop table if exists tag;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists audience_manager_authentication_seq;

drop sequence if exists blog_post_seq;

drop sequence if exists comment_seq;

drop sequence if exists company_size_seq;

drop sequence if exists industry_seq;

drop sequence if exists tag_seq;

drop sequence if exists user_seq;

