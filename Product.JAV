// --- DATA AND RENDERING LOGIC ---

// CSV data embedded from the uploaded file snippet
const csvData = `Title,Author,Publisher,Price (RM),Quantity,Genre,Year 
Harry Potter and the Half-Blood Prince,J.K. Rowling,Bloomsbury,39.9,10,Fantasy,2005
Harry Potter and the Deathly Hallows,J.K. Rowling,Bloomsbury,42.5,10,Fantasy,2007
A Feast for Crows,George R.R. Martin,Bantam Books,45,10,Fantasy,2005
A Dance with Dragons,George R.R. Martin,Bantam Books,48,10,Fantasy,2011
The Tales of Beedle the Bard,J.K. Rowling,Bloomsbury,35,10,Fantasy,2008
The Casual Vacancy,J.K. Rowling,"Little, Brown",40,10,Drama,2012
The Fault in Our Stars,John Green,Penguin,35,10,Romance,2013
Paper Towns,John Green,Penguin,33,10,Mystery,2014
The Maze Runner,James Dashner,Delacorte Press,37,10,Fiction,2015
The Scorch Trials,James Dashner,Delacorte Press,37,10,Fiction,2009
The Death Cure,James Dashner,Delacorte Press,37,10,Fiction,2013
The Silent Patient,Alex Michaelides,Orion,39,10,Thriller,2017
Gone Girl,Gillian Flynn,Crown Publishing,40,10,Thriller,2006
The Midnight Library,Matt Haig,Canongate,42,10,Fiction,2020
Lawak Kampus,Keith,Gempak Starz,15.9,10,Comedy,2013
Candy Series: Cinta Muka Buku,Suri Ryana,Gempak Starz,21.9,10,Romance,2019
Dendam Pontianak,Ben Wong,Gempak Starz,19.9,10,Horror,2022
X-VENTURE Underworld,Slaium,Gempak Starz,22.9,10,Fantasy,2013
Lawak Kingdom,Keith,Gempak Starz,17.9,10,Comedy,2019
Fixi Retro: Jerat,Sybella Nor,Fixi,26.9,10,Thriller,2020
Langit Vanilla,Wani Ardy,Fixi,27.9,10,Romance,2015
Ngeri,Shaz Johar,Fixi,26.9,10,Thriller,2017
Tombiruo,Ramlee Awang Murshid,Fixi,30,10,Action,2015
Misi Fizik - Edisi Kemas Kini (2023),Tim MISI,Gempak Starz,9.9,10,Fiction,2017
Misi 10A - Edisi Kemas Kini,Tim MISI,Gempak Starz,9.9,10,Fiction,2019
Misi 4 Flat - Edisi Kemas Kini,Tim MISI,Gempak Starz,14.9,10,Fiction,2021
`;

let allProducts = [];
let productsPerPage = 10;
let currentPage = 0;

/**
 * Simple CSV parser to convert string data into an array of product objects.
 * @param {string} csv - The CSV string data.
 * @returns {Array<Object>} - Array of product objects.
 */
function parseCSV(csv) {
    const lines = csv.split('\n').filter(line => line.trim() !== '');
    if (lines.length === 0) return [];
    
    // Basic header parsing and normalization
    const headers = lines[0].split(',').map(h => h.trim().replace(/\s\(RM\)/, 'Price').replace(/\s/g, ''));
    const products = [];

    for (let i = 1; i < lines.length; i++) {
        // Simple regex to handle fields that might contain commas if enclosed in quotes
        const values = lines[i].match(/(?:"[^"]*"|[^,])+/g).map(v => v.trim().replace(/"/g, ''));

        if (values.length === headers.length) {
            const product = {};
            headers.forEach((header, index) => {
                product[header] = values[index];
            });
            
            // Convert price to number and calculate mock discounts
            const listPrice = parseFloat(product.Price);
            if (!isNaN(listPrice)) {
                // Random discount between 50% and 85% for demonstration
                const discountPercent = Math.floor(Math.random() * (85 - 50 + 1)) + 50; 
                const salePrice = (listPrice * (1 - discountPercent / 100)).toFixed(2);
                product.listPrice = listPrice.toFixed(2);
                product.salePrice = salePrice;
                product.discountPercent = discountPercent;
            }

            products.push(product);
        }
    }
    return products;
}

/**
 * Generates the HTML string for a single product card.
 * @param {Object} product - The product data object.
 * @returns {string} - HTML string for the product card.
 */
function createProductCard(product) {
    // Determine placeholder image color based on genre (for visual variety)
    let color = '3b82f6'; 
    if (product.Genre === 'Fantasy') color = '8B5CF6'; 
    if (product.Genre === 'Romance') color = 'EC4899'; 
    if (product.Genre === 'Thriller') color = '1F2937'; 
    
    const coverPlaceholder = `https://placehold.co/150x230/${color}/ffffff?text=BOOK+COVER`;

    return `
        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition duration-300 relative overflow-hidden group">
            <!-- Sale Badge -->
            <div class="sale-badge absolute top-2 right-2 px-3 py-1 text-xs font-bold text-white rounded-md shadow-md z-10">
                -${product.discountPercent}%
            </div>
            
            <!-- Book Cover Placeholder -->
            <div class="h-48 sm:h-64 flex items-center justify-center bg-gray-200 overflow-hidden">
                <img src="${coverPlaceholder}" alt="${product.Title} cover" class="object-cover h-full w-auto shadow-lg group-hover:scale-105 transition duration-500">
            </div>
            
            <div class="p-3">
                <h4 class="text-sm font-semibold truncate hover:whitespace-normal" title="${product.Title}">${product.Title}</h4>
                <p class="text-xs text-gray-500 mb-2 truncate" title="${product.Author}">by ${product.Author}</p>
                
                <!-- Pricing (Discount Emphasis) -->
                <div class="flex items-center justify-between">
                    <span class="text-xl font-extrabold text-red-600">RM ${product.salePrice}</span>
                    <span class="text-sm text-gray-400 line-through">RM ${product.listPrice}</span>
                </div>

                <!-- CTA -->
                <button class="w-full mt-2 py-1.5 text-xs font-semibold rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition duration-150">
                    Add to Cart
                </button>
            </div>
        </div>
    `;
}

/**
 * Renders the products currently assigned to the page.
 */
function renderProducts() {
    const container = document.getElementById('product-grid-container');
    const start = currentPage * productsPerPage;
    const end = start + productsPerPage;
    const productsToRender = allProducts.slice(start, end);

    let html = productsToRender.map(createProductCard).join('');
    
    // If it's the first page, replace content; otherwise, append
    if (currentPage === 0) {
        container.innerHTML = html;
    } else {
        container.insertAdjacentHTML('beforeend', html);
    }
    
    // Update button visibility
    const loadMoreBtn = document.getElementById('load-more-btn');
    if (end >= allProducts.length) {
        loadMoreBtn.classList.add('hidden');
    } else {
        loadMoreBtn.classList.remove('hidden');
    }
}

/**
 * Increments the page and renders the next set of products.
 */
function handleLoadMore() {
    currentPage++;
    renderProducts();
}


// --- MAIN INITIALIZATION ---

window.onload = function () {
    // 1. Initialize icons (Lucide Icons)
    lucide.createIcons();

    // 2. Set current year in footer
    document.getElementById('current-year').textContent = new Date().getFullYear();

    // 3. Process data
    allProducts = parseCSV(csvData);

    // 4. Initial rendering
    renderProducts();

    // 5. Attach Load More listener
    const loadMoreBtn = document.getElementById('load-more-btn');
    if (loadMoreBtn) {
        loadMoreBtn.addEventListener('click', handleLoadMore);
    }
};
