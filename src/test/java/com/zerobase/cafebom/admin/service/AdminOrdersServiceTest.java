package com.zerobase.cafebom.admin.service;

import static com.zerobase.cafebom.exception.ErrorCode.ORDERS_ALREADY_CANCELED;
import static com.zerobase.cafebom.exception.ErrorCode.ORDERS_COOKING_TIME_ALREADY_SET;
import static com.zerobase.cafebom.exception.ErrorCode.ORDERS_NOT_RECEIVED_STATUS;
import static com.zerobase.cafebom.exception.ErrorCode.ORDERS_STATUS_ONLY_NEXT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.zerobase.cafebom.exception.CustomException;
import com.zerobase.cafebom.orders.domain.Orders;
import com.zerobase.cafebom.orders.domain.OrdersRepository;
import com.zerobase.cafebom.orders.dto.OrdersCookingTimeModifyDto;
import com.zerobase.cafebom.orders.dto.OrdersReceiptModifyDto;
import com.zerobase.cafebom.orders.dto.OrdersStatusModifyDto;
import com.zerobase.cafebom.type.OrdersCookingStatus;
import com.zerobase.cafebom.type.OrdersCookingTime;
import com.zerobase.cafebom.type.OrdersReceiptStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class AdminOrdersServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AdminOrdersService adminOrdersService;

    @Mock
    private OrdersRepository ordersRepository;

    // minsu-23.09.20
    @Test
    @DisplayName("주문 상태 변경 실패 - 주문 상태는 전으로 돌아갈 수 없음")
    void failUpdateOrdersStatusOnlyNext() {
        // given
        Long ordersId = 1L;
        given(ordersRepository.findById(ordersId))
            .willReturn(
                Optional.of(Orders.builder().cookingStatus(OrdersCookingStatus.COOKING).build()));

        OrdersStatusModifyDto modifyDto = OrdersStatusModifyDto.builder()
            .newStatus(OrdersCookingStatus.NONE)
            .build();

        // then
        CustomException exception = assertThrows(CustomException.class,
            () -> adminOrdersService.modifyOrdersStatus(ordersId, modifyDto));
        assertThat(exception.getErrorCode()).isEqualTo(ORDERS_STATUS_ONLY_NEXT);
    }

    // minsu-23.09.20
    @Test
    @DisplayName("주문 수락/거절 실패 - 주문이 이미 취소된 경우")
    void failOrdersReceiptAlreadyCanceled() {
        // given
        Long ordersId = 1L;

        given(ordersRepository.findById(ordersId))
            .willReturn(
                Optional.of(Orders.builder().receiptStatus(OrdersReceiptStatus.CANCELED).build()));

        OrdersReceiptModifyDto modifyDto = OrdersReceiptModifyDto.builder()
            .newReceiptStatus(OrdersReceiptStatus.RECEIVED)
            .build();

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> adminOrdersService.modifyOrdersReceiptStatus(ordersId, modifyDto));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ORDERS_ALREADY_CANCELED);
    }

    // minsu-23.09.20
    @Test
    @DisplayName("관리자 조리 예정 시간 선택 실패 - 주문이 수락되지 않은 경우")
    void failAdminOrdersCookingTimeModifyNotReceived() {
        // given
        Long ordersId = 1L;
        given(ordersRepository.findById(ordersId))
            .willReturn(
                Optional.of(Orders.builder().receiptStatus(OrdersReceiptStatus.CANCELED).build()));

        OrdersCookingTimeModifyDto modifyDto = OrdersCookingTimeModifyDto.builder()
            .selectedCookingTime(OrdersCookingTime._5_TO_10_MINUTES)
            .build();

        // then
        CustomException exception = assertThrows(CustomException.class,
            () -> adminOrdersService.modifyOrdersCookingTime(ordersId, modifyDto));
        assertThat(exception.getErrorCode()).isEqualTo(ORDERS_NOT_RECEIVED_STATUS);
    }

    // minsu-23.09.20
    @Test
    @DisplayName("관리자 조리 예정 시간 선택 실패 - 이미 조리 예정 시간이 설정되어 있는 경우")
    void failAdminOrdersCookingTimeModifyAlreadySet() {
        // given
        Long ordersId = 1L;
        Orders orders = Orders.builder()
            .id(ordersId)
            .cookingStatus(OrdersCookingStatus.COOKING)
            .receiptStatus(OrdersReceiptStatus.RECEIVED)
            .cookingTime(OrdersCookingTime._5_TO_10_MINUTES) // Already set
            .receivedTime(LocalDateTime.now())
            .build();

        given(ordersRepository.findById(ordersId)).willReturn(Optional.of(orders));

        OrdersCookingTimeModifyDto modifyDto = OrdersCookingTimeModifyDto.builder()
            .selectedCookingTime(OrdersCookingTime.OVER_25_MINUTES)
            .build();

        // then
        CustomException exception = assertThrows(CustomException.class,
            () -> adminOrdersService.modifyOrdersCookingTime(ordersId, modifyDto));
        assertThat(exception.getErrorCode()).isEqualTo(ORDERS_COOKING_TIME_ALREADY_SET);
    }
}