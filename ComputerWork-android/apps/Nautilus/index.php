<?php
require_once("nautilusConfig.php");
require_once("../M/mfiles.php");
require_once("Nautilus.class.php");
/*------------------------------------------------------------*/
global $Mview;
global $Mmodel;
$Mview = new Mview;
$Mmodel = new Mmodel;
/*------------------------------------------------------------*/
$nt = new Nautilus;
$nt->control();
/*------------------------------------------------------------*/
?>
