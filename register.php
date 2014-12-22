<?php
require_once("baglan.php");//database bağlantısı gercekleştirdik
 if (isset($_GET["regId"])) { //Kontrol
  
 $regId = $_GET['regId']; //GET ile gelen regId değerini aldık
  
 $sql = "INSERT INTO wp_gcm_kullanicilar (registration_id) values ('$regId')"; //regId yi database kaydedicek sorgu
  
 if(!mysqli_query($con, $sql)){//sorguyu çalıştırdık
  die('MySQL query failed'.mysql_error());
    }
 }
mysqli_close($con);//mysql connection kapattık
  
  
?>
