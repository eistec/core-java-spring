USE `arrowhead`;

REVOKE ALL, GRANT OPTION FROM 'configuration'@'localhost';

GRANT ALL PRIVILEGES ON `arrowhead`.`configfiles` TO 'configuration'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`logs` TO 'configuration'@'localhost';

GRANT ALL PRIVILEGES ON `arrowhead`.`configfiles` TO 'configuration'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`logs` TO 'configuration'@'%';

FLUSH PRIVILEGES;
