<?php
require 'db_config.php'; // استدعاء ملف الاتصال بقاعدة البيانات

header('Content-Type: application/json; charset=utf-8'); // ضبط الترميز للـ JSON

if (isset($_GET['latitude']) && isset($_GET['longitude'])) {
    $user_lat = floatval($_GET['latitude']);
    $user_lon = floatval($_GET['longitude']);

    // تأكد أن الاتصال يستخدم utf8mb4
    $conn->set_charset("utf8mb4");

    // الاستعلام عن المطاعم القريبة مع حساب المسافة
    $sql = "SELECT id, name, latitude, longitude, address, phone, 
                   (6371 * ACOS(
                        COS(RADIANS(?)) * COS(RADIANS(latitude)) * 
                        COS(RADIANS(longitude) - RADIANS(?)) + 
                        SIN(RADIANS(?)) * SIN(RADIANS(latitude))
                    )) AS distance
            FROM restaurants
            ORDER BY distance ASC";

    // تحضير الاستعلام لمنع هجمات الـ SQL Injection
    if ($stmt = $conn->prepare($sql)) {
        $stmt->bind_param("ddd", $user_lat, $user_lon, $user_lat);
        $stmt->execute();
        $result = $stmt->get_result();

        $restaurants = [];
        while ($row = $result->fetch_assoc()) {
            $restaurants[] = $row;
        }

        // إرسال النتيجة بصيغة JSON
        echo json_encode(["restaurants" => $restaurants], JSON_UNESCAPED_UNICODE);
        
        // إغلاق الاستعلام
        $stmt->close();
    } else {
        // إرسال خطأ في حالة فشل الاستعلام
        http_response_code(500);
        echo json_encode(["error" => "فشل في جلب البيانات من قاعدة البيانات"], JSON_UNESCAPED_UNICODE);
    }
} else {
    // إرسال خطأ إذا كانت المعطيات غير مكتملة
    http_response_code(400);
    echo json_encode(["error" => "يرجى إدخال الإحداثيات الصحيحة"], JSON_UNESCAPED_UNICODE);
}

// إغلاق الاتصال بقاعدة البيانات
$conn->close();
?>