package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.model.CategoryItem
import com.example.model.Product
import com.example.ui.components.ProductCard

@Composable
fun CategoryExplorerScreen(
    categories: List<CategoryItem>,
    selectedCategoryId: String,
    onSelectCategory: (String) -> Unit,
    products: List<Product>,
    wishlistProductIds: Set<String>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }

    val currentCategory = categories.find { it.id == selectedCategoryId } ?: categories.firstOrNull()
    val subcategories = currentCategory?.subcategories ?: emptyList()

    val filteredProducts = remember(products, selectedCategoryId, selectedSubcategory) {
        var list = if (selectedCategoryId == "all") products else products.filter { it.categoryId.equals(selectedCategoryId, ignoreCase = true) }
        if (selectedSubcategory != null) {
            list = list.filter { it.subcategory.equals(selectedSubcategory, ignoreCase = true) }
        }
        list
    }

    Row(modifier = modifier.fillMaxSize()) {
        // Master Category Rail / Column (on wide screen or compact top row)
        if (isWideScreen) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .width(240.dp)
                    .fillMaxHeight()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "All Departments",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    items(categories) { cat ->
                        val isSelected = cat.id == selectedCategoryId
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectCategory(cat.id)
                                    selectedSubcategory = null
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                val iconVector = when (cat.id) {
                                    "electronics" -> Icons.Default.Devices
                                    "fashion" -> Icons.Default.Checkroom
                                    "home" -> Icons.Default.Home
                                    "beauty" -> Icons.Default.Face
                                    "sports" -> Icons.Default.FitnessCenter
                                    else -> Icons.Default.ShoppingBasket
                                }
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = cat.name,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Product Catalog Pane
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // If phone screen, show category chip carousel on top
            if (!isWideScreen) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat.id == selectedCategoryId
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onSelectCategory(cat.id)
                                selectedSubcategory = null
                            },
                            label = { Text(cat.name) }
                        )
                    }
                }
            }

            // Subcategories horizontal pills
            if (subcategories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubcategory == null,
                            onClick = { selectedSubcategory = null },
                            label = { Text("All ${currentCategory?.name ?: ""}") }
                        )
                    }
                    items(subcategories) { sub ->
                        FilterChip(
                            selected = selectedSubcategory == sub,
                            onClick = { selectedSubcategory = sub },
                            label = { Text(sub) }
                        )
                    }
                }
            }

            // Products Grid
            val columns = if (isWideScreen) 3 else 2
            val chunkedProducts = filteredProducts.chunked(columns)

            LazyColumn(
                contentPadding = PaddingValues(bottom = 90.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("category_products_list")
            ) {
                item {
                    Text(
                        text = if (selectedSubcategory != null) selectedSubcategory!! else currentCategory?.name ?: "All Products",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(chunkedProducts) { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        for (prod in rowItems) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod,
                                    isInWishlist = wishlistProductIds.contains(prod.id),
                                    onProductClick = { onProductClick(prod) },
                                    onAddToCartClick = { onAddToCart(prod) },
                                    onToggleWishlist = { onToggleWishlist(prod) }
                                )
                            }
                        }
                        if (rowItems.size < columns) {
                            for (i in 0 until (columns - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
