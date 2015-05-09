# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table additions (
  id                        bigint not null,
  ui_label                  varchar(255),
  additional_price          float,
  constraint pk_additions primary key (id))
;

create table admin_referal_code (
  id                        bigint not null,
  full_name                 varchar(255),
  code                      varchar(255) not null,
  admin_id                  bigint,
  constraint pk_admin_referal_code primary key (id))
;

create table admin_referal_earning (
  id                        bigint not null,
  admin_earning_value       float,
  date_earned               timestamp,
  admin_referal_code_id     bigint,
  orders_order_id           bigint,
  constraint pk_admin_referal_earning primary key (id))
;

create table admin_sent_mail (
  admin_sent_mail_id        bigint not null,
  admin_user_admin_user_id  bigint,
  message                   text not null,
  sent_to                   varchar(255) not null,
  sent_on                   timestamp not null,
  constraint pk_admin_sent_mail primary key (admin_sent_mail_id))
;

create table admin_user (
  admin_user_id             bigint not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  salt                      varchar(255),
  active                    boolean default 'true',
  admin_user_offset         varchar(255),
  constraint uq_admin_user_email unique (email),
  constraint pk_admin_user primary key (admin_user_id))
;

create table authorised_user (
  id                        bigint not null,
  user_name                 varchar(255),
  constraint pk_authorised_user primary key (id))
;

create table client (
  id                        bigint not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  email                     varchar(255),
  c_email                   varchar(255),
  password                  varchar(64),
  salt                      varchar(64),
  country_code              varchar(255),
  area_code                 varchar(255),
  phone_number              varchar(255),
  alternative_phone         varchar(255),
  receive_company_mail      boolean,
  client_time_zone          varchar(255),
  client_time_zone_offset   varchar(255),
  client_time_zone_real     varchar(20),
  created_on                timestamp,
  country_id                bigint,
  constraint pk_client primary key (id))
;

create table client_referal_earning (
  id                        bigint not null,
  referal_earning_value     float not null,
  date_earned               timestamp,
  referral_code_referral_code_id bigint,
  orders_order_id           bigint,
  constraint pk_client_referal_earning primary key (id))
;

create table client_sent_mail (
  client_sent_mail_id       bigint not null,
  client_id                 bigint,
  message                   text not null,
  sent_to                   varchar(255) not null,
  sent_on                   timestamp not null,
  constraint pk_client_sent_mail primary key (client_sent_mail_id))
;

create table countries (
  id                        bigint not null,
  iso                       varchar(255),
  name                      varchar(255),
  nicename                  varchar(255),
  iso3                      varchar(255),
  numcode                   integer,
  phonecode                 integer,
  constraint pk_countries primary key (id))
;

create table deadline_deadline_category_association (
  id                        bigint not null,
  order_deadlines_id        bigint,
  order_deadline_category_id bigint,
  additional_price          float,
  constraint pk_deadline_deadline_category_as primary key (id))
;

create table file_type (
  id                        bigint not null,
  product_file_type         integer,
  description               varchar(255),
  test                      varchar(255),
  constraint ck_file_type_product_file_type check (product_file_type in (0,1,2,3,4,5)),
  constraint pk_file_type primary key (id))
;

create table fine_type (
  id                        bigint not null,
  fine_name                 varchar(255) not null,
  fine_percentage           integer,
  fine_description          varchar(255),
  constraint pk_fine_type primary key (id))
;

create table freelance_writer (
  freelance_writer_id       bigint not null,
  writer_id                 bigint,
  full_name                 varchar(255),
  email                     varchar(255),
  phone_number              varchar(255),
  constraint pk_freelance_writer primary key (freelance_writer_id))
;

create table order_cpp_mode (
  id                        bigint not null,
  order_cpp_mode_name       integer,
  cpp_mode_description      varchar(255),
  constraint ck_order_cpp_mode_order_cpp_mode_name check (order_cpp_mode_name in (0,1,2)),
  constraint pk_order_cpp_mode primary key (id))
;

create table order_currence (
  order_currency_id         bigint not null,
  currency_name             varchar(255),
  currency_symbol           varchar(255),
  currency_symbol_2         varchar(255),
  convertion_rate           float,
  constraint pk_order_currence primary key (order_currency_id))
;

create table order_deadline_category (
  id                        bigint not null,
  order_deadline_category_name varchar(255),
  order_deadline_category_description varchar(255),
  constraint pk_order_deadline_category primary key (id))
;

create table order_deadlines (
  id                        bigint not null,
  deadline_value            integer,
  deadline_unit             varchar(255),
  seconds_elapsing_to_deadline bigint,
  constraint pk_order_deadlines primary key (id))
;

create table order_document_type (
  id                        bigint not null,
  document_type_name        varchar(255),
  base_price                float,
  description               varchar(255),
  additions_factor          float,
  order_deadline_category_id bigint,
  order_cpp_mode_id         bigint,
  constraint pk_order_document_type primary key (id))
;

create table order_files (
  id                        bigint not null,
  owner                     integer not null,
  file_size                 bigint not null,
  file_name                 varchar(255) not null,
  file_sent_to              integer not null,
  storage_path              varchar(255) not null,
  content_type              varchar(255) not null,
  upload_date               timestamp not null,
  order_file                bytea,
  orders_order_id           bigint,
  constraint ck_order_files_owner check (owner in (0,1,2)),
  constraint ck_order_files_file_sent_to check (file_sent_to in (0,1,2)),
  constraint pk_order_files primary key (id))
;

create table order_fines (
  id                        bigint not null,
  fine_date                 timestamp,
  amount                    float,
  removed                   boolean,
  fine_type_id              bigint,
  orders_order_id           bigint,
  constraint pk_order_fines primary key (id))
;

create table order_level_of_writing (
  id                        bigint not null,
  order_level               varchar(255),
  additional_price          float,
  description               varchar(255),
  constraint pk_order_level_of_writing primary key (id))
;

create table order_messages (
  id                        bigint not null,
  msg_to                    integer,
  msg_from                  integer,
  status                    boolean default false not null,
  action_required           boolean default false not null,
  action_taken              boolean default false not null,
  message_promise_value     varchar(255) not null,
  message_type              integer,
  orders_order_id           bigint,
  sent_on                   timestamp not null,
  message                   text,
  constraint ck_order_messages_msg_to check (msg_to in (0,1,2)),
  constraint ck_order_messages_msg_from check (msg_from in (0,1,2)),
  constraint ck_order_messages_message_type check (message_type in (0,1,2)),
  constraint pk_order_messages primary key (id))
;

create table order_product_files (
  id                        bigint not null,
  owner                     integer not null,
  file_size                 bigint not null,
  file_name                 varchar(255) not null,
  file_sent_to              integer not null,
  storage_path              varchar(255),
  content_type              varchar(255) not null,
  upload_date               timestamp not null,
  download_date             timestamp,
  product_file_type         integer,
  has_been_downloaded       boolean,
  plagiarism                integer not null,
  orders_order_id           bigint,
  constraint ck_order_product_files_owner check (owner in (0,1,2)),
  constraint ck_order_product_files_file_sent_to check (file_sent_to in (0,1,2)),
  constraint ck_order_product_files_product_file_type check (product_file_type in (0,1,2,3,4,5)),
  constraint pk_order_product_files primary key (id))
;

create table order_revision (
  id                        bigint not null,
  revision_instruction      text not null,
  orders_order_id           bigint,
  constraint pk_order_revision primary key (id))
;

create table order_subject (
  id                        bigint not null,
  subject_name              varchar(255),
  description               varchar(255),
  order_subject_category_id bigint,
  constraint pk_order_subject primary key (id))
;

create table order_subject_category (
  id                        bigint not null,
  subject_category_name     varchar(255),
  additional_price          float,
  description               varchar(255),
  constraint pk_order_subject_category primary key (id))
;

create table orders (
  order_id                  bigint not null,
  order_code                bigint,
  document_deadline         integer,
  topic                     varchar(255),
  order_instruction         text,
  number_of_units           integer,
  writing_style             varchar(255),
  no_of_references          integer,
  operating_system          varchar(255),
  prog_language             varchar(255),
  database                  varchar(255),
  prefered_writer           varchar(255),
  order_date                timestamp,
  order_deadline            timestamp,
  order_total               float,
  amount_paid               float,
  is_paid                   boolean,
  is_writer_assigned        boolean,
  is_complete               boolean,
  is_closed                 boolean,
  on_revision               boolean,
  client_feedback           integer,
  additional_pages          integer,
  additional_pages_value    float,
  is_additional_pages_paid  boolean,
  source_domain             varchar(255),
  invoice_id                varchar(255),
  approved                  boolean,
  coupon_code               varchar(255),
  prefered_writer_value     float,
  prefered_writer_value_paid boolean,
  client_id                 bigint,
  order_level_of_writing_id bigint,
  order_document_type_id    bigint,
  order_currence_order_currency_id bigint,
  spacing_id                bigint,
  order_subject_id          bigint,
  freelance_writer_freelance_writer_id bigint,
  referral_code_referral_code_id bigint,
  constraint pk_orders primary key (order_id))
;

create table preferred_writer (
  preferred_writer_entry_id bigint not null,
  client_id                 bigint,
  freelance_writer_freelance_writer_id bigint,
  constraint pk_preferred_writer primary key (preferred_writer_entry_id))
;

create table referral_code (
  referral_code_id          bigint not null,
  code                      varchar(255) not null,
  date_generated            timestamp not null,
  client_id                 bigint,
  constraint pk_referral_code primary key (referral_code_id))
;

create table reset_password (
  id                        bigint not null,
  token                     varchar(255) not null,
  date_generated            timestamp not null,
  used                      boolean,
  client_id                 bigint,
  constraint pk_reset_password primary key (id))
;

create table security_role (
  id                        bigint not null,
  name                      varchar(255) not null,
  constraint uq_security_role_name unique (name),
  constraint pk_security_role primary key (id))
;

create table spacing (
  id                        bigint not null,
  spacing                   varchar(255),
  alias                     varchar(255),
  factor                    integer,
  constraint pk_spacing primary key (id))
;

create table user_permission (
  id                        bigint not null,
  permission_value          varchar(255),
  constraint pk_user_permission primary key (id))
;

create table writer_support (
  id                        bigint not null,
  f_name                    varchar(255),
  constraint pk_writer_support primary key (id))
;


create table admin_user_security_role (
  admin_user_admin_user_id       bigint not null,
  security_role_id               bigint not null,
  constraint pk_admin_user_security_role primary key (admin_user_admin_user_id, security_role_id))
;

create table admin_user_user_permission (
  admin_user_admin_user_id       bigint not null,
  user_permission_id             bigint not null,
  constraint pk_admin_user_user_permission primary key (admin_user_admin_user_id, user_permission_id))
;

create table authorised_user_security_role (
  authorised_user_id             bigint not null,
  security_role_id               bigint not null,
  constraint pk_authorised_user_security_role primary key (authorised_user_id, security_role_id))
;

create table authorised_user_user_permission (
  authorised_user_id             bigint not null,
  user_permission_id             bigint not null,
  constraint pk_authorised_user_user_permission primary key (authorised_user_id, user_permission_id))
;

create table order_subject_order_document_typ (
  order_subject_id               bigint not null,
  order_document_type_id         bigint not null,
  constraint pk_order_subject_order_document_typ primary key (order_subject_id, order_document_type_id))
;

create table orders_additions (
  orders_order_id                bigint not null,
  additions_id                   bigint not null,
  constraint pk_orders_additions primary key (orders_order_id, additions_id))
;
create sequence additions_seq;

create sequence admin_referal_code_seq;

create sequence admin_referal_earning_seq;

create sequence admin_sent_mail_seq;

create sequence admin_user_seq;

create sequence authorised_user_seq;

create sequence client_seq;

create sequence client_referal_earning_seq;

create sequence client_sent_mail_seq;

create sequence countries_seq;

create sequence deadline_deadline_category_association_seq;

create sequence file_type_seq;

create sequence fine_type_seq;

create sequence freelance_writer_seq;

create sequence order_cpp_mode_seq;

create sequence order_currence_seq;

create sequence order_deadline_category_seq;

create sequence order_deadlines_seq;

create sequence order_document_type_seq;

create sequence order_files_seq;

create sequence order_fines_seq;

create sequence order_level_of_writing_seq;

create sequence order_messages_seq;

create sequence order_product_files_seq;

create sequence order_revision_seq;

create sequence order_subject_seq;

create sequence order_subject_category_seq;

create sequence orders_seq;

create sequence preferred_writer_seq;

create sequence referral_code_seq;

create sequence reset_password_seq;

create sequence security_role_seq;

create sequence spacing_seq;

create sequence user_permission_seq;

create sequence writer_support_seq;

alter table admin_referal_earning add constraint fk_admin_referal_earning_admin_1 foreign key (admin_referal_code_id) references admin_referal_code (id);
create index ix_admin_referal_earning_admin_1 on admin_referal_earning (admin_referal_code_id);
alter table admin_referal_earning add constraint fk_admin_referal_earning_order_2 foreign key (orders_order_id) references orders (order_id);
create index ix_admin_referal_earning_order_2 on admin_referal_earning (orders_order_id);
alter table admin_sent_mail add constraint fk_admin_sent_mail_adminUser_3 foreign key (admin_user_admin_user_id) references admin_user (admin_user_id);
create index ix_admin_sent_mail_adminUser_3 on admin_sent_mail (admin_user_admin_user_id);
alter table client add constraint fk_client_country_4 foreign key (country_id) references countries (id);
create index ix_client_country_4 on client (country_id);
alter table client_referal_earning add constraint fk_client_referal_earning_refe_5 foreign key (referral_code_referral_code_id) references referral_code (referral_code_id);
create index ix_client_referal_earning_refe_5 on client_referal_earning (referral_code_referral_code_id);
alter table client_referal_earning add constraint fk_client_referal_earning_orde_6 foreign key (orders_order_id) references orders (order_id);
create index ix_client_referal_earning_orde_6 on client_referal_earning (orders_order_id);
alter table client_sent_mail add constraint fk_client_sent_mail_client_7 foreign key (client_id) references client (id);
create index ix_client_sent_mail_client_7 on client_sent_mail (client_id);
alter table deadline_deadline_category_association add constraint fk_deadline_deadline_category__8 foreign key (order_deadlines_id) references order_deadlines (id);
create index ix_deadline_deadline_category__8 on deadline_deadline_category_association (order_deadlines_id);
alter table deadline_deadline_category_association add constraint fk_deadline_deadline_category__9 foreign key (order_deadline_category_id) references order_deadline_category (id);
create index ix_deadline_deadline_category__9 on deadline_deadline_category_association (order_deadline_category_id);
alter table order_document_type add constraint fk_order_document_type_orderD_10 foreign key (order_deadline_category_id) references order_deadline_category (id);
create index ix_order_document_type_orderD_10 on order_document_type (order_deadline_category_id);
alter table order_document_type add constraint fk_order_document_type_orderC_11 foreign key (order_cpp_mode_id) references order_cpp_mode (id);
create index ix_order_document_type_orderC_11 on order_document_type (order_cpp_mode_id);
alter table order_files add constraint fk_order_files_orders_12 foreign key (orders_order_id) references orders (order_id);
create index ix_order_files_orders_12 on order_files (orders_order_id);
alter table order_fines add constraint fk_order_fines_fineType_13 foreign key (fine_type_id) references fine_type (id);
create index ix_order_fines_fineType_13 on order_fines (fine_type_id);
alter table order_fines add constraint fk_order_fines_orders_14 foreign key (orders_order_id) references orders (order_id);
create index ix_order_fines_orders_14 on order_fines (orders_order_id);
alter table order_messages add constraint fk_order_messages_orders_15 foreign key (orders_order_id) references orders (order_id);
create index ix_order_messages_orders_15 on order_messages (orders_order_id);
alter table order_product_files add constraint fk_order_product_files_orders_16 foreign key (orders_order_id) references orders (order_id);
create index ix_order_product_files_orders_16 on order_product_files (orders_order_id);
alter table order_revision add constraint fk_order_revision_orders_17 foreign key (orders_order_id) references orders (order_id);
create index ix_order_revision_orders_17 on order_revision (orders_order_id);
alter table order_subject add constraint fk_order_subject_orderSubject_18 foreign key (order_subject_category_id) references order_subject_category (id);
create index ix_order_subject_orderSubject_18 on order_subject (order_subject_category_id);
alter table orders add constraint fk_orders_client_19 foreign key (client_id) references client (id);
create index ix_orders_client_19 on orders (client_id);
alter table orders add constraint fk_orders_orderLevelOfWriting_20 foreign key (order_level_of_writing_id) references order_level_of_writing (id);
create index ix_orders_orderLevelOfWriting_20 on orders (order_level_of_writing_id);
alter table orders add constraint fk_orders_orderDocumentType_21 foreign key (order_document_type_id) references order_document_type (id);
create index ix_orders_orderDocumentType_21 on orders (order_document_type_id);
alter table orders add constraint fk_orders_orderCurrence_22 foreign key (order_currence_order_currency_id) references order_currence (order_currency_id);
create index ix_orders_orderCurrence_22 on orders (order_currence_order_currency_id);
alter table orders add constraint fk_orders_spacing_23 foreign key (spacing_id) references spacing (id);
create index ix_orders_spacing_23 on orders (spacing_id);
alter table orders add constraint fk_orders_orderSubject_24 foreign key (order_subject_id) references order_subject (id);
create index ix_orders_orderSubject_24 on orders (order_subject_id);
alter table orders add constraint fk_orders_freelanceWriter_25 foreign key (freelance_writer_freelance_writer_id) references freelance_writer (freelance_writer_id);
create index ix_orders_freelanceWriter_25 on orders (freelance_writer_freelance_writer_id);
alter table orders add constraint fk_orders_referralCode_26 foreign key (referral_code_referral_code_id) references referral_code (referral_code_id);
create index ix_orders_referralCode_26 on orders (referral_code_referral_code_id);
alter table preferred_writer add constraint fk_preferred_writer_client_27 foreign key (client_id) references client (id);
create index ix_preferred_writer_client_27 on preferred_writer (client_id);
alter table preferred_writer add constraint fk_preferred_writer_freelance_28 foreign key (freelance_writer_freelance_writer_id) references freelance_writer (freelance_writer_id);
create index ix_preferred_writer_freelance_28 on preferred_writer (freelance_writer_freelance_writer_id);
alter table referral_code add constraint fk_referral_code_client_29 foreign key (client_id) references client (id);
create index ix_referral_code_client_29 on referral_code (client_id);
alter table reset_password add constraint fk_reset_password_client_30 foreign key (client_id) references client (id);
create index ix_reset_password_client_30 on reset_password (client_id);



alter table admin_user_security_role add constraint fk_admin_user_security_role_a_01 foreign key (admin_user_admin_user_id) references admin_user (admin_user_id);

alter table admin_user_security_role add constraint fk_admin_user_security_role_s_02 foreign key (security_role_id) references security_role (id);

alter table admin_user_user_permission add constraint fk_admin_user_user_permission_01 foreign key (admin_user_admin_user_id) references admin_user (admin_user_id);

alter table admin_user_user_permission add constraint fk_admin_user_user_permission_02 foreign key (user_permission_id) references user_permission (id);

alter table authorised_user_security_role add constraint fk_authorised_user_security_r_01 foreign key (authorised_user_id) references authorised_user (id);

alter table authorised_user_security_role add constraint fk_authorised_user_security_r_02 foreign key (security_role_id) references security_role (id);

alter table authorised_user_user_permission add constraint fk_authorised_user_user_permi_01 foreign key (authorised_user_id) references authorised_user (id);

alter table authorised_user_user_permission add constraint fk_authorised_user_user_permi_02 foreign key (user_permission_id) references user_permission (id);

alter table order_subject_order_document_typ add constraint fk_order_subject_order_docume_01 foreign key (order_subject_id) references order_subject (id);

alter table order_subject_order_document_typ add constraint fk_order_subject_order_docume_02 foreign key (order_document_type_id) references order_document_type (id);

alter table orders_additions add constraint fk_orders_additions_orders_01 foreign key (orders_order_id) references orders (order_id);

alter table orders_additions add constraint fk_orders_additions_additions_02 foreign key (additions_id) references additions (id);

# --- !Downs

drop table if exists additions cascade;

drop table if exists orders_additions cascade;

drop table if exists admin_referal_code cascade;

drop table if exists admin_referal_earning cascade;

drop table if exists admin_sent_mail cascade;

drop table if exists admin_user cascade;

drop table if exists admin_user_security_role cascade;

drop table if exists admin_user_user_permission cascade;

drop table if exists authorised_user cascade;

drop table if exists authorised_user_security_role cascade;

drop table if exists authorised_user_user_permission cascade;

drop table if exists client cascade;

drop table if exists client_referal_earning cascade;

drop table if exists client_sent_mail cascade;

drop table if exists countries cascade;

drop table if exists deadline_deadline_category_association cascade;

drop table if exists file_type cascade;

drop table if exists fine_type cascade;

drop table if exists freelance_writer cascade;

drop table if exists order_cpp_mode cascade;

drop table if exists order_currence cascade;

drop table if exists order_deadline_category cascade;

drop table if exists order_deadlines cascade;

drop table if exists order_document_type cascade;

drop table if exists order_subject_order_document_typ cascade;

drop table if exists order_files cascade;

drop table if exists order_fines cascade;

drop table if exists order_level_of_writing cascade;

drop table if exists order_messages cascade;

drop table if exists order_product_files cascade;

drop table if exists order_revision cascade;

drop table if exists order_subject cascade;

drop table if exists order_subject_category cascade;

drop table if exists orders cascade;

drop table if exists preferred_writer cascade;

drop table if exists referral_code cascade;

drop table if exists reset_password cascade;

drop table if exists security_role cascade;

drop table if exists spacing cascade;

drop table if exists user_permission cascade;

drop table if exists writer_support cascade;

drop sequence if exists additions_seq;

drop sequence if exists admin_referal_code_seq;

drop sequence if exists admin_referal_earning_seq;

drop sequence if exists admin_sent_mail_seq;

drop sequence if exists admin_user_seq;

drop sequence if exists authorised_user_seq;

drop sequence if exists client_seq;

drop sequence if exists client_referal_earning_seq;

drop sequence if exists client_sent_mail_seq;

drop sequence if exists countries_seq;

drop sequence if exists deadline_deadline_category_association_seq;

drop sequence if exists file_type_seq;

drop sequence if exists fine_type_seq;

drop sequence if exists freelance_writer_seq;

drop sequence if exists order_cpp_mode_seq;

drop sequence if exists order_currence_seq;

drop sequence if exists order_deadline_category_seq;

drop sequence if exists order_deadlines_seq;

drop sequence if exists order_document_type_seq;

drop sequence if exists order_files_seq;

drop sequence if exists order_fines_seq;

drop sequence if exists order_level_of_writing_seq;

drop sequence if exists order_messages_seq;

drop sequence if exists order_product_files_seq;

drop sequence if exists order_revision_seq;

drop sequence if exists order_subject_seq;

drop sequence if exists order_subject_category_seq;

drop sequence if exists orders_seq;

drop sequence if exists preferred_writer_seq;

drop sequence if exists referral_code_seq;

drop sequence if exists reset_password_seq;

drop sequence if exists security_role_seq;

drop sequence if exists spacing_seq;

drop sequence if exists user_permission_seq;

drop sequence if exists writer_support_seq;

