<?php
include 'db_config.php';

$restaurant_id = $_GET['restaurant_id'];
$query = "SELECT * FROM offers WHERE restaurant_id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $restaurant_id);
$stmt->execute();
$result = $stmt->get_result();

$offers = array();
while ($row = $result->fetch_assoc()) {
    $offers[] = $row;
}

echo json_encode(["success" => true, "offers" => $offers]);
$stmt->close();
$conn->close();
?>
