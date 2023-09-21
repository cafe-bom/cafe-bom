package com.zerobase.cafebom.admin.order.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import com.zerobase.cafebom.admin.service.AdminOrdersService;
import io.swagger.annotations.ApiOperation;
import com.zerobase.cafebom.front.order.dto.OrdersCookingTimeModifyDto;
import com.zerobase.cafebom.front.order.dto.OrdersCookingTimeModifyForm;
import com.zerobase.cafebom.front.order.dto.OrdersReceiptModifyDto;
import com.zerobase.cafebom.front.order.dto.OrdersReceiptModifyForm;
import com.zerobase.cafebom.front.order.dto.OrdersStatusModifyDto;
import com.zerobase.cafebom.front.order.dto.OrdersStatusModifyForm;
import com.zerobase.cafebom.front.order.service.impl.OrdersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "admin-orders-controller", description = "관리자 주문 관련 API")
@RestController
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminOrdersController {

    private final AdminOrdersService adminOrdersService;

    // minsu-23.09.20
    @ApiOperation(value = "주문 상태 변경", notes = "관리자가 주문 상태를 변경합니다.")
    @PatchMapping("/status/{ordersId}")
    public ResponseEntity<Void> ordersStatusModify(
        @PathVariable Long ordersId,
        @RequestBody @Valid OrdersStatusModifyForm ordersStatusModifyForm) {

        adminOrdersService.modifyOrdersStatus(ordersId,
            OrdersStatusModifyDto.from(ordersStatusModifyForm));
        return ResponseEntity.status(NO_CONTENT).build();
    }

    // minsu-23.09.20
    @ApiOperation(value = "주문 수락 또는 거절", notes = "관리자가 주문을 수락 또는 거절합니다.")
    @PatchMapping("/receipt-status/{ordersId}")
    public ResponseEntity<Void> ordersReceiptModify(
        @PathVariable Long ordersId,
        @RequestBody @Valid OrdersReceiptModifyForm ordersReceiptModifyForm) {

        adminOrdersService.modifyOrdersReceiptStatus(ordersId,
            OrdersReceiptModifyDto.from(ordersReceiptModifyForm));
        return ResponseEntity.status(NO_CONTENT).build();
    }

    // minsu-23.09.20
    @ApiOperation(value = "주문 조리 예정 시간 선택", notes = "관리자가 수락된 주문 조리 예정 시간을 선택합니다.")
    @PatchMapping("/cooking-time/{ordersId}")
    public ResponseEntity<Void> ordersCookingTimeModify(
        @PathVariable Long ordersId,
        @RequestBody @Valid OrdersCookingTimeModifyForm cookingTimeModifyForm) {

        adminOrdersService.modifyOrdersCookingTime(ordersId,
            OrdersCookingTimeModifyDto.from(cookingTimeModifyForm));
        return ResponseEntity.status(NO_CONTENT).build();
    }
}