-- -- -- On 3 August 2014
CREATE TYPE writer_highest_education_level AS ENUM('Diploma','Bachelors','Master','Doctorate');
CREATE TYPE writer_specialities AS ENUM('Academic','Technical','SEO');
CREATE TYPE writer_levels AS ENUM ('Novice','Jurnior','Intermediate','Pro','Maestro');
CREATE TYPE writer_availability ('Full_time','Part_time');
CREATE TYPE writing_styles AS ENUM ('APA','MLA','Chicago','Harvard','IEEE');
CREATE TYPE writer_disciplines AS ENUM('History','Mathematics','Statistics','IT','Computer Science','Engineering');
CREATE TYPE writer_status AS ENUM('active','probation','suspended','terminated');
CREATE TYPE payment_method AS ENUM('Paypal','MPesa','Wire Transfer','Visa','MasterCard');
CREATE TYPE test_type AS ENUM('qa','essay');--qa stands for question answer
CREATE TYPE project_variables AS ENUM('Proofread by Editor','Fulfilled by a top 10 writer', 'V.I.P. Support','Writer immediate assign');
CREATE TYPE project_spacing AS ENUM('Single','Double');
CREATE TYPE project_level AS ENUM('High School','Bachelors','Master','Doctorate');
CREATE TYPE project_writer_category AS ENUM('Any Specialist Writer','ESL Premium', 'ENL Premium');
CREATE TYPE project_assignment_status AS ENUM('writer assigned','client cancelled','writer unassigned','writer cancelled','on hold','completed', 'returned');
CREATE TYPE completed_project_file_stage AS ENUM('Draft','Final Product', 'Product Revision');
CREATE TYPE writer_project_status AS ENUM('Completed','Preparing','Reassignment Request', 'On hold', 'Forced Reassignment', 'Finable Forced Reassignment'); 
CREATE TYPE client_approved_status AS ENUM('Not Yet','Approved','Declined');
CREATE TYPE writer_fine_status ('effected','disputed');
CREATE TYPE admin_title AS ENUM('Editor','Account Manager','General Manager','Customer Care Support','Marketing Manager','Finance Officer'); 

CREATE TABLE writer(
	writer_id serial PRIMARY KEY,
	writer_f_name varchar(32) NOT NULL,
	writer_l_name varchar(32) NOT NULL,
	writer_title, --MR, Dr etc
	writer_p_email varchar(32) NOT NULL,
	writer_t_email varchar(32),
	writer_username varchar(32) NOT NULL,
	writer_password varchar(64) NOT NULL,
	writer_password_salt varchar(64) NOT NULL,
	writer_phone_number varchar (12) NOT NULL,
	writer_highest_qualification writer_highest_education_level NOT NULL,  	
	writer_country integer NOT NULL,
	writer_speciality writer_specialities[] NOT NULL,
	writer_current_occupation varchar(32) NOT NULL,
	writer_level writer_levels NOT NULL,
	writer_availability writer_availability NOT NULL,
	writer_writing_styles writing_styles[] NOT NULL,
	writer_disciplines writer_disciplines[] NOT NULL,
	writer_cv_path varchar(32) NOT NULL,
	writer_certificate_path varchar(32) NOT NULL,
	writer_national_ID_path varchar(32) NOT NULL,
	writer_date_of_reg timestamp DEFAULT now(),
	writer_email_confirmed Boolean NOT NULL DEFAULT FALSE,
	writer_qa_attempted Boolean NOT NULL DEFAULT FALSE,
	writer_qa_passed Boolean NOT NULL DEFAULT FALSE,
	writer_essay_attempted Boolean NOT NULL DEFAULT FALSE,
	writer_accepted Boolean NOT NULL DEFAULT FALSE,
	writer_status writer_status NOT NULL,
	writer_phone_verified Boolean NOT NULL DEFAULT FALSE,
	writer_primary_payment_method payment_method
);

CREATE TYPE correct_answer AS ENUM('a','b','c','d');
CREATE TYPE qa_category AS ENUM('technical','essay');
CREATE TYPE qa_section AS ENUM('citation','grammar');

CREATE TABLE qa_test(
	qa_test_id serial PRIMARY KEY,
	qa_test_question text NOT NULL,
	qa_category qa_category NOT NULL,
	qa_section qa_section NOT NULL,
	qa_answer_a text NOT NULL,
	qa_answer_b text NOT NULL,
	qa_answer_c text NOT NULL,
	qa_answer_d text NOT NULL,
	qa_correct_answer correct_answer NOT NULL
);

CREATE TABLE essay_test(
	essay_test_id serial PRIMARY KEY,
	essay_test_question text NOT NULL,
	essay_test_style writing_styles NOT NULL
);

CREATE TABLE ongoing_test(
	ongoing_test_id serial PRIMARY KEY,
	ongoing_test_writer_id INTEGER NOT NULL REFERENCES writer(writer_id),
	ongoing_test_start_time timestamp NOT NULL DEFAULT now(),
	ongoing_test_end_time timestamp NOT NULL DEFAULT now(),--To be fixed later
	ongoing_test_type test_type NOT NULL
);
-- On 9, August, 2014

CREATE TABLE client(
	client_id serial PRIMARY KEY,
	client_f_name varchar(32) NOT NULL,
	client_l_name varchar(32) NOT NULL,
	client_email varchar(32) NOT NULL,
	client_phone_number varchar(32) NOT NULL,
	client_country INTEGER NOT NULL REFERENCES countries(country_id),
	client_password varchar(64),
	client_password_hash varchar(64),
	client_state_province varchar(32)
);

CREATE TABLE project_type(
	project_type_id serial PRIMARY KEY,
	project_type_description text NOT NULL,
	project_base_rate real NOT NULL
);

CREATE TABLE project_variable(
	project_variable_id serial PRIMARY KEY,
	project_variable_name project_variables NOT NULL,
	project_variable_rate real NOT NULL
);

CREATE TABLE free_features(
	free_feature_id serial PRIMARY KEY,
	free_feature_title text NOT NULL,
	free_feature_value real NOT NULL
);

CREATE TABLE project_urgency(
	project_urgency_id serial PRIMARY KEY,
	project_urgency_title varchar(32) NOT NULL,
	project_urgency_rate double(10,2) NOT NULL 
	
);

CREATE TABLE project_levels(

	project_level_id serial PRIMARY KEY,
	project_level_title project_level,
	project_level_rate double(10,2)
);

CREATE TABLE currency(
	currency_id serial PRIMARY KEY,
	currency_name varchar(32) NOT NULL,
	currency_symbol varchar(16) NOT NULL,
	currency_rate	double(10,2)
);

CREATE TABLE project_writer_type(
	project_writer_type_id serial PRIMARY KEY,
	project_writer_title project_writer_type,
	project_writer_rate double(10,2)
);

CREATE TABLE project(
	project_id serial PRIMARY KEY,
	project_type_id NOT NULL REFERENCES project_type(project_type_id),
	project_client_id NOT NULL REFERENCES client(client_id),
	project_variables project_variables[]
	project_spacing project_spacing,
	project_urgency_id NOT NULL REFERENCES project_urgency(project_urgency_id),
	project_topic text NOT NULL,
	project_description text NOT NULL,
	project_number_of_pages int NOT NULL,
	project_level_id int NOT NULL REFERENCES project_levels(project_level_id),
	project_currency_id int NOT NULL REFERENCES currency(currency_id);
	project_writer_category	project_writer_category NOT NULL,
	project_writer_type_id int NOT NULL REFERENCES project_writer_type(project_writer_id),
	project_payment_status boolean NOT NULL DEFAULT FALSE,
	project_payment_amount double(10,2),
	project_payment_date timestamp,
	project_assignment_status project_assignment_status NOT NULL DEFAULT 'writer unassigned',
	project_payment_transaction_id varchar(64),
	project_payment_method payment_method,
	project_returned_new_deadline timestamp,
	project_compensation_rate double(10,2) NOT NULL, 
	project_step int NOT NULL DEFAULT 0 --The project step represents the level at which the project is from initiation = 0 to n where n is 1,2...and is revision n
); 


CREATE TABLE project_files(
	project_file_id serial PRIMARY KEY,
	project_id int NOT NULL REFERENCES project(project_id),
	project_file_path varchar(64)  NULL,
	project_file_type varchar(16)
);

-- On 10 August 2014
---Files of the completed order: where should they be?
---Which order is assigned to which writer?
CREATE TABLE completed_project_files(
	completed_project_file_id serial PRIMARY KEY,
	project_id int NOT NULL REFERENCES project(project_id),
	project_step int NOT NULL,
	completed_project_file_path varchar(64) NOT NULL,
	completed_project_file_time_uploaded timestamp NOT NULL DEFAULT now();
	completed_project_file_type varchar
	completed_project_file_stage completed_project_file_stage, --for lack of a better word, stage here represents either draft or final product
	completed_project_file_writer_id int NOT NULL REFERENCES writer(writer_id)
);

CREATE TABLE project_step_instructions(
	project_step_instruction_id serial PRIMARY KEY,
	project_step_instruction_project_id int NOT NULL REFERENCES project(project_id),
	project_step_instruction_text text
);


CREATE TABLE writer_project(
	writer_project_id serial PRIMARY KEY,
	writer_project_writer_id int NOT NULL REFERENCES writer(writer_id),
	writer_project_project_id int NOT NULL REFERENCES project(project_id),
	writer_project_status writer_project_status,
	writer_project_status_reason text,
	writer_project_compensation_rate double(10,2) NOT NULL,
	writer_project_compensation_pages int NOT NULL,
	writer_project_client_approved_status client_approved_status,
	writer_project_completed_date timestamp,
	writer_project_bonus_amount double(10,2) NOT NULL DEFAULT 0.00,
	writer_project_payment_status writer_project_payment_status
);

CREATE TABLE admins(
	admin_id serial PRIMARY KEY,
	admin_f_name varchar(32) NOT NULL,
	admin_l_name varchar(32) NOT NULL,
	admin_level admin_level NOT NULL DEFAULT 'normal',
	admin_email varchar(32) NOT NULL,
	admin_phone_number varchar(16) NOT NULL,
	admin_password varchar(64) NOT NULL,
	admin_title admin_title NOT NULL,
	admin_password_salt varchar(64) NOT NULL,
	admin_account_status boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE writer_fines(
	writer_fine_id serial PRIMARY KEY,
	writer_fine_project_id int NOT NULL REFERENCES writer_project(writer_project_id),
	writer_fine_value double(10,2) NOT NULL,
	writer_fine_status writer_fine_status,
	writer_fine_effector int NOT NULL REFERENCES admins(admin_id),
	writer_fine_reason text
);
CREATE TABLE writer_earnings(
	writer_earnings_id serial PRIMARY KEY,
	writer_earnings_project_id int NOT NULL REFERENCES project(project_id),
	writer_earnings_payment_period varchar(64) NOT NULL,
	
);




