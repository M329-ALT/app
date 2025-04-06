
<?php
include 'db_config.php';

$restaurant_id = $_POST['restaurant_id'];
$name = $_POST['name'];
$location = $_POST['location'];
$image_url = $_POST['image_url'];

$query = "UPDATE restaurants SET name = '$name', location = '$location', image = '$image_url' WHERE id = '$restaurant_id'";

if (mysqli_query($conn, $query)) {
    echo json_encode(["success" => true, "message" => "تم تحديث بيانات المطعم بنجاح"]);
} else {
    echo json_encode(["success" => false, "message" => "فشل تحديث بيانات المطعم"]);
}
?>