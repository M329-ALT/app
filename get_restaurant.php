<?php
include 'db_config.php';

$owner_id = $_GET['owner_id'];

$query = "SELECT * FROM restaurants WHERE owner_id = '$owner_id'";
$result = mysqli_query($conn, $query);
$restaurant = mysqli_fetch_assoc($result);

if ($restaurant) {
    echo json_encode(["success" => true, "restaurant" => $restaurant]);
} else {
    echo json_encode(["success" => false, "message" => "لا يوجد مطعم مرتبط بهذا الحساب."]);
}
?>