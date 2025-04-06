<?php
include 'db_config.php';

$user_id = $_POST['user_id'];
$meals = json_decode($_POST['meals'], true);
$total_price = $_POST['total_price'];

$query = "INSERT INTO orders (user_id, total_price) VALUES ('$user_id', '$total_price')";
if (mysqli_query($conn, $query)) {
    $order_id = mysqli_insert_id($conn);
    foreach ($meals as $meal) {
        $meal_id = $meal['id'];
        $price = $meal['price'];
        mysqli_query($conn, "INSERT INTO order_items (order_id, meal_id, price) VALUES ('$order_id', '$meal_id', '$price')");
    }
    echo json_encode(["success" => true, "order_id" => $order_id]);
} else {
    echo json_encode(["success" => false, "message" => "فشل إنشاء الطلب"]);
}
?>
