package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FilterCriteria
import com.example.model.SortOption
import com.example.ui.theme.AccentGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(

    currentFilter: FilterCriteria,
    availableBrands: List<String>,
    onApply: (FilterCriteria) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var minPrice by remember { mutableStateOf(currentFilter.minPrice.toFloat()) }
    var maxPrice by remember { mutableStateOf(currentFilter.maxPrice.toFloat()) }
    var selectedBrands by remember { mutableStateOf(currentFilter.selectedBrands.toMutableSet()) }
    var minRating by remember { mutableStateOf(currentFilter.minRating) }
    var minDiscount by remember { mutableStateOf(currentFilter.minDiscount) }
    var inStockOnly by remember { mutableStateOf(currentFilter.inStockOnly) }
    var primeOnly by remember { mutableStateOf(currentFilter.primeDeliveryOnly) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Filter Products",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                TextButton(onClick = {
                    minPrice = 0f
                    maxPrice = 200000f
                    selectedBrands = mutableSetOf()
                    minRating = 0f
                    minDiscount = 0
                    inStockOnly = false
                    primeOnly = false
                    onReset()
                }) {
                    Text("Reset All")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Price Range Slider
            Text(
                text = "Price Range (₹0 - ₹${maxPrice.toInt()})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Slider(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                valueRange = 1000f..150000f,
                steps = 20,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating Filter
            Text(
                text = "Minimum Customer Rating",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(0f to "Any", 4.0f to "4★ & above", 4.5f to "4.5★ & above").forEach { (ratingVal, label) ->
                    FilterChip(
                        selected = minRating == ratingVal,
                        onClick = { minRating = ratingVal },
                        label = { Text(label) },
                        leadingIcon = if (ratingVal > 0f) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Brands Filter
            if (availableBrands.isNotEmpty()) {
                Text(
                    text = "Brand",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableBrands.forEach { brand ->
                        val isSelected = selectedBrands.contains(brand)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val updated = selectedBrands.toMutableSet()
                                if (isSelected) updated.remove(brand) else updated.add(brand)
                                selectedBrands = updated
                            },
                            label = { Text(brand) },
                            leadingIcon = if (isSelected) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Discount
            Text(
                text = "Minimum Discount",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "All", 20 to "20%+", 35 to "35%+", 50 to "50%+").forEach { (discVal, label) ->
                    FilterChip(
                        selected = minDiscount == discVal,
                        onClick = { minDiscount = discVal },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fast Delivery & Stock Toggles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Prime Express Delivery Only", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = primeOnly, onCheckedChange = { primeOnly = it })
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("In Stock Only", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = inStockOnly, onCheckedChange = { inStockOnly = it })
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Apply Button
            Button(
                onClick = {
                    onApply(
                        FilterCriteria(
                            minPrice = minPrice.toDouble(),
                            maxPrice = maxPrice.toDouble(),
                            selectedBrands = selectedBrands,
                            minRating = minRating,
                            minDiscount = minDiscount,
                            inStockOnly = inStockOnly,
                            primeDeliveryOnly = primeOnly
                        )
                    )
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_filter_btn")
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSort: SortOption,
    onSelectSort: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SortOption.values().forEach { option ->
                val isSelected = currentSort == option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onSelectSort(option)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = option.displayName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
