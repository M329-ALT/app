
<?php
include 'db_config.php';

$offer_id = $_POST['offer_id'];
$restaurant_id = $_POST['restaurant_id'];
$name = $_POST['name'];
$price = $_POST['price'];
$description = $_POST['description'];
$image_url = $_POST['image_url'];

if ($offer_id == 0) { // إضافة عرض جديد
    $query = "INSERT INTO offers (restaurant_id, name, price, description, image) VALUES ('$restaurant_id', '$name', '$price', '$description', '$image_url')";
} else { // تعديل العرض الحالي
    $query = "UPDATE offers SET name = '$name', price = '$price', description = '$description', image = '$image_url' WHERE id = '$offer_id'";
}

if (mysqli_query($conn, $query)) {
    echo json_encode(["success" => true, "message" => "تم تحديث العرض بنجاح"]);
} else {
    echo json_encode(["success" => false, "message" => "فشل تحديث العرض"]);
}
?>