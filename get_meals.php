<?php
require 'db_config.php'; // استدعاء ملف الاتصال بقاعدة البيانات

header('Content-Type: application/json; charset=utf-8'); // تحديد الترميز للـ JSON

if (isset($_GET['id'])) {
    $restaurant_id = intval($_GET['id']);

    // ضبط الترميز utf8mb4 للاتصال
    $conn->set_charset("utf8mb4");

    // الاستعلام عن الوجبات الخاصة بالمطعم المحدد
    $sql = "SELECT id, name, price, description, image_url FROM meals WHERE restaurant_id = ?";

    if ($stmt = $conn->prepare($sql)) {
        $stmt->bind_param("i", $restaurant_id);
        $stmt->execute();
        $result = $stmt->get_result();

        $meals = [];
        while ($row = $result->fetch_assoc()) {
            $meals[] = $row;
        }

        if (!empty($meals)) {
            echo json_encode(["meals" => $meals], JSON_UNESCAPED_UNICODE);
        } else {
            http_response_code(404);
            echo json_encode(["error" => "لا توجد وجبات متاحة لهذا المطعم"], JSON_UNESCAPED_UNICODE);
        }

        $stmt->close();
    } else {
        http_response_code(500);
        echo json_encode(["error" => "خطأ في تنفيذ الاستعلام"], JSON_UNESCAPED_UNICODE);
    }
} else {
    http_response_code(400);
    echo json_encode(["error" => "يرجى تحديد معرف المطعم"], JSON_UNESCAPED_UNICODE);
}

$conn->close();
?>
