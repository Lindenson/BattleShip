CREATE TABLE IF NOT EXISTS `yes`.`USER_AUTHENTICATION` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    `password` VARCHAR(90) NOT NULL,
    `enabled` BOOLEAN NOT NULL,
    `won` INT NOT NULL,
    PRIMARY KEY (`id`));


select if (
               exists(
                   select distinct index_name from information_schema.statistics
                   where table_name = 'USER_AUTHENTICATION' and index_name like 'ix_user_username'
               )
           ,'select ''ix_user_username'' _______;'
           ,'create unique index ix_user_username on `USER_AUTHENTICATION` (username)') into @a;
PREPARE stmt1 FROM @a;
EXECUTE stmt1;
DEALLOCATE PREPARE stmt1;

CREATE TABLE IF NOT EXISTS `yes`.`USER_AUTHORIZATION` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NOT NULL,
    `role` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`id`),
    constraint fk_authorities_users foreign key(username) references USER_AUTHENTICATION(username));
select if (
               exists(
                   select distinct index_name from information_schema.statistics
                   where table_name = 'USER_AUTHORIZATION' and index_name like 'ix_auth_username'
               )
           ,'select ''ix_auth_username'' _______;'
           ,'create unique index ix_auth_username on USER_AUTHORIZATION (username, role)') into @b;
PREPARE stmt2 FROM @b;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

