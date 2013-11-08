<?php
/*------------------------------------------------------------*/
$serverName = $_SERVER['SERVER_NAME'];

define('M_HOST', 'localhost');
if ( $serverName == "puma" || $serverName == "192.168.1.42" ) {
	define('M_DIR', '/home/engine/src/svn/M');
	define('M_URL', '/M');
	define('M_USER', 'msdb');
	define('M_PASSWORD', 'msdb');
	define('M_DBNAME', 'tas');
} elseif ( strstr($serverName, 'theora.com') ) {
	define('M_DIR', '/home/ohadalon/public_html/M');
	define('M_URL', '/M');
	define('M_USER', 'ohadalon');
	define('M_PASSWORD', 'ohad1961');
	define('M_DBNAME', 'ohadalon_tas');
} else {
	echo(__FILE__.":".__LINE__.": $serverName: No server configuration");
}
/*------------------------------------------------------------*/
?>
