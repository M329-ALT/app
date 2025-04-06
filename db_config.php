<?php
$host = "localhost";
$user = "root";  // اسم المستخدم الافتراضي
$password = "";  // اتركه فارغًا إذا لم تغيره
$dbname = "best_resturants";

$conn = new mysqli($host, $user, $password, $dbname);
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die("فشل الاتصال: " . $conn->connect_error);
}
?>
