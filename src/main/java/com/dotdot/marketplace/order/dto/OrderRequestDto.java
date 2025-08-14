package com.dotdot.marketplace.order.dto;

import com.dotdot.marketplace.order.entity.OrderStatus;
import com.dotdot.marketplace.orderitem.dto.OrderItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private OrderStatus status;
    private long user;
    private List<OrderItemRequestDto> orderItems;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OrderStatus status;
        private long user;
        private List<OrderItemRequestDto> orderItems;

        public Builder withStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder withUser(long user) {
            this.user = user;
            return this;
        }

        public Builder withOrderItems(List<OrderItemRequestDto> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public OrderRequestDto build() {
            OrderRequestDto dto = new OrderRequestDto();
            dto.setStatus(status);
            dto.setUser(user);
            dto.setOrderItems(orderItems);
            return dto;
        }
    }

}
