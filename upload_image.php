<?php
include 'db_config.php';

$upload_dir = "uploads/";
if (!file_exists($upload_dir)) {
    mkdir($upload_dir, 0777, true);
}

if ($_FILES['image']['error'] == 0) {
    $image_name = time() . "_" . basename($_FILES['image']['name']);
    $image_path = $upload_dir . $image_name;

    if (move_uploaded_file($_FILES['image']['tmp_name'], $image_path)) {
        echo json_encode(["success" => true, "image_url" => "http://yourserver.com/$image_path"]);
    } else {
        echo json_encode(["success" => false, "message" => "فشل رفع الصورة"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "لم يتم استلام الصورة بشكل صحيح"]);
}
?>