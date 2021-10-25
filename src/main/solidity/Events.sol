// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

interface Events {
    event BuyOrderCreated(address indexed wallet, string stockId, uint256 orderId, uint256 amount, uint256 timestamp);
    event SellOrderCreated(address indexed wallet, string stockId, uint256 orderId, uint256 amount, uint256 timestamp);
    event OrderSettled();
}
